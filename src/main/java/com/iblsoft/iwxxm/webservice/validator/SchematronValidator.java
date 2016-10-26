/***********************************************************************************************
 *
 * Copyright (C) 2016, IBL Software Engineering spol. s r. o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * ---------------------------------------------------------------------------------------------
 *
 * Notice:
 *
 * This file is based on source code from Command-line Refuter of Unshapely XML (CRUX)
 * developed by Developed by NCAR's Research Applications Laboratory (http://www.ral.ucar.edu)
 *
 * Copyright (c) 2016. University Corporation for Atmospheric Research (UCAR). All rights reserved.
 * See also https://github.com/NCAR/crux/blob/master/LICENSE for more information about CRUX license.
 *
 ***********************************************************************************************/

package com.iblsoft.iwxxm.webservice.validator;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.TreeModel;
import net.sf.saxon.s9api.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validates XML files against Schematron rules.  This uses the ISO Schematron XSLT files, which are then executed by
 * Saxon.
 *
 * This class currently calls Saxon via the main() method.  While it is possible to call sub-classes of Saxon directly,
 * the Transform class which is called by the main() method is complicated and not very easy to figure out how to use
 * directly.  An improvement would be to call the Saxon classes directly rather than through main()
 */
class SchematronValidator {

    private static Map<String, String> schematronTransformCache = new HashMap<>();

    public ValidationResult validate(String inputXml, String schematronFile) throws Exception {
        SaxonTransform saxonTransform = new SaxonTransform();
        ErrorListener errorListener = new ErrorListener();
        String validationXsltContent = getSchematronXslt(schematronFile);
        XsltBundle validationXslt = new XsltBundle(validationXsltContent);
        saxonTransform.transform(validationXslt, inputXml, errorListener);

        if (errorListener.hasErrors()) {
            return new ValidationResult("", errorListener.getErrors());
        } else {
            return new ValidationResult("", null);
        }
    }

    private String loadResourceFile(String resourcePath) {
        try (InputStream stream = this.getClass().getResourceAsStream(resourcePath)) {
            return IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XsltBundle createSchematronXslt() {
        String messageXslt = this.loadResourceFile("/iso-schematron-xslt2/iso_schematron_message_xslt2.xsl");
        String schematronSkeletonXslt = this.loadResourceFile("/iso-schematron-xslt2/iso_schematron_skeleton_for_saxon.xsl");
        return new XsltBundle(messageXslt)
                .addImport("iso_schematron_skeleton_for_saxon.xsl", schematronSkeletonXslt);
    }

    private synchronized String getSchematronXslt(String schematronFile) {
        String xslt = schematronTransformCache.get(schematronFile);
        if (xslt == null) {
            SaxonTransform saxonTransform = new SaxonTransform();
            XsltBundle schematronXslt = createSchematronXslt();

            XdmNode output;
            try {
                String schematronRules = FileUtils.readFileToString(new File(schematronFile));
                output = saxonTransform.transform(schematronXslt, schematronRules, null);
            } catch (Exception e) {
                return null;
            }
            xslt = output.toString();
            schematronTransformCache.put(schematronFile, xslt);
        }
        return xslt;
    }

    /**
     * Helper class to handle transformation errors.
     */
    private static class ErrorListener implements javax.xml.transform.ErrorListener, MessageListener {
        private List<ValidationError> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        private ErrorListener() {
        }

        public List<ValidationError> getErrors() {
            return errors;
        }

        @Override
        public void warning(TransformerException exception) throws TransformerException {
            warnings.add(String.format("Warning on line %d col %d: %s", exception.getLocator().getLineNumber(), exception.getLocator().getColumnNumber(), exception.getMessage()));
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            errors.add(translateException(exception));
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            errors.add(translateException(exception));
        }

        @Override
        public void message(XdmNode xdmNode, boolean b, SourceLocator sourceLocator) {
            if (sourceLocator != null) {
                errors.add(new ValidationError(xdmNode.toString(), "", sourceLocator.getLineNumber(), sourceLocator.getColumnNumber()));
            } else {
                errors.add(new ValidationError(xdmNode.toString(), "", null, null));
            }
        }

        public boolean hasErrors() {
            return errors.size() > 0;
        }

        private ValidationError translateException(TransformerException e) {
            SourceLocator locator = e.getLocator();
            if (locator != null) {
                return new ValidationError(e.getMessage(), "", locator.getLineNumber(), locator.getColumnNumber());
            }

            return new ValidationError(e.getMessage(), "", null, null);
        }
    }

    /**
     * Helper class to handle Saxon XSLT transfromation.
     */
    private static class SaxonTransform {
        final private XsltCompiler xsltCompiler;
        private Processor processor = null;

        public SaxonTransform() {
            this.processor = new Processor(new Configuration());
            this.xsltCompiler = this.processor.newXsltCompiler();
        }

        public XdmNode transform(XsltBundle xslt, String sourceContent, ErrorListener errorListener) throws IOException, SaxonApiException {
            XsltTransformer xsltTransformer = null;
            try {
                XsltExecutable xsltExecutable = compileXslt(xslt);
                xsltTransformer = xsltExecutable.load();
                if (errorListener != null) {
                    xsltTransformer.setErrorListener(errorListener);
                    xsltTransformer.setMessageListener(errorListener);
                }

                XdmNode source = this.createXdmNodeFromString(sourceContent);
                xsltTransformer.setInitialContextNode(source);
                XdmDestination resultTree = new XdmDestination();
                xsltTransformer.setDestination(resultTree);
                xsltTransformer.transform();

                return resultTree.getXdmNode();
            } finally {
                if (xsltTransformer != null) {
                    xsltTransformer.close();
                }
            }
        }

        private XsltExecutable compileXslt(XsltBundle xslt) throws SaxonApiException {
            try (StringReader reader = new StringReader(xslt.getMainXsltContent())) {
                StreamSource source = new StreamSource(reader);
                xsltCompiler.setURIResolver(xslt);
                return xsltCompiler.compile(source);
            }
        }

        private XdmNode createXdmNodeFromString(String xmlAsString) throws IOException, SaxonApiException {
            try (InputStream inputStream = new ByteArrayInputStream(xmlAsString.getBytes(StandardCharsets.UTF_8))) {
                DocumentBuilder documentBuilder = processor.newDocumentBuilder();
                documentBuilder.setTreeModel(TreeModel.TINY_TREE);
                StreamSource source = new StreamSource(inputStream);
                return documentBuilder.build(source);
            }
        }
    }

    /**
     * Helper class which allows to process XSLT with imports from other files.
     */
    private static class XsltBundle implements URIResolver {
        private String mainXsltContent;
        private Map<String, String> importableUris = new HashMap<>();

        public XsltBundle(String mainXsltContent) {
            this.mainXsltContent = mainXsltContent;
        }

        public String getMainXsltContent() {
            return mainXsltContent;
        }

        public final XsltBundle addImport(String systemId, String xsltContent) {
            importableUris.put(systemId, xsltContent);
            return this;
        }

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            if (importableUris.containsKey(href)) {
                String contentAsString = importableUris.get(href);
                return new StreamSource(new StringReader(contentAsString));
            } else {
                throw new TransformerException("Cannot resolve uri: " + href);
            }
        }
    }
}