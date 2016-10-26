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

import org.xml.sax.*;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Validator of XML and XSD files against XML schema 1.0
 */
class XML10Validator {
    private EntityResolver2 resolver;

    public XML10Validator(String... catalogLocations) {
        if (catalogLocations.length > 0) {
            resolver = new XmlCatalogResolver(catalogLocations, true);
        }
    }

    public ValidationResult validate(Reader reader) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        ErrorHandler errorHandler = new ErrorHandler();
        VersionDetectionHandler versionDetectionHandler = new VersionDetectionHandler();

        SAXParser parser = factory.newSAXParser();
        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

        XMLReader xmlReader = parser.getXMLReader();
        if (resolver != null) {
            xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);
        }

        xmlReader.setErrorHandler(errorHandler);
        xmlReader.setContentHandler(versionDetectionHandler);
        xmlReader.parse(new InputSource(reader));

        return new ValidationResult(versionDetectionHandler.getVersion(), errorHandler.getFailures());
    }

    /**
     * Detects the version of IWXXM message. Version is taken from the first XML element which namespace
     * is starting with http://icao.int/iwxxm/. Detected version is then returned by getVersion() method.
     */
    private static class VersionDetectionHandler extends DefaultHandler {
        private static final String IWXXM_NAMESPACE_PATTERN = "HTTP://ICAO.INT/IWXXM/";
        private String version;

        public String getVersion() {
            return version;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (version == null) {
                if (uri != null && uri.toUpperCase(Locale.ROOT).startsWith(IWXXM_NAMESPACE_PATTERN)) {
                    version = uri.substring(IWXXM_NAMESPACE_PATTERN.length());
                }
            }
        }
    }

    /**
     * Gathers the warnings and errors into a list of ValidationErrors
     */
    private static class ErrorHandler implements org.xml.sax.ErrorHandler {
        private List<ValidationError> failures = new ArrayList<ValidationError>();

        public List<ValidationError> getFailures() {
            return failures;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            failures.add(translateException(exception));
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            failures.add(translateException(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            failures.add(translateException(exception));
        }

        private ValidationError translateException(SAXParseException exception) {
            return new ValidationError(exception.getMessage(), "", exception.getLineNumber(), exception.getColumnNumber());
        }
    }
}