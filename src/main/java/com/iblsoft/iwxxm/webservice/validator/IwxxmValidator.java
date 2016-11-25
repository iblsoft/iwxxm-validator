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

import com.google.common.base.VerifyException;
import com.iblsoft.iwxxm.webservice.util.Log;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * Performs XSD and schematron validation of IWXXM messages.
 */
public class IwxxmValidator {

    private final File validationCatalogFile;
    private final File validationRulesDir;
    private final String defaultIwxxmVersion;
    private final Pattern iwxxmVersionPattern = Pattern.compile("^[0-9a-zA-Z_-]+(\\.[0-9a-zA-Z]+)*$");
    private SchematronValidator schematronValidator = new SchematronValidator();

    public IwxxmValidator(File validationCatalogFile, File validationRulesDir, String defaultIwxxmVersion) {
        this.validationCatalogFile = checkNotNull(validationCatalogFile);
        this.validationRulesDir = checkNotNull(validationRulesDir);
        this.defaultIwxxmVersion = nullToEmpty(defaultIwxxmVersion);
    }

    public ValidationResult validate(String iwxxmData, String iwxxmVersion) {
        Log.INSTANCE.debug("IwxxmWebService.validate request started");

        if (iwxxmVersion == null) {
            iwxxmVersion = defaultIwxxmVersion;
        }

        String catalogFile = validationCatalogFile.getAbsolutePath();
        XML10Validator validator = new XML10Validator(catalogFile);

        List<ValidationError> errors = new ArrayList<>();
        try {
            printValidatingXMLSchema(catalogFile);
            ValidationResult xsdValidationResult;
            try (StringReader reader = new StringReader(iwxxmData)) {
                xsdValidationResult = validator.validate(reader);
                errors.addAll(xsdValidationResult.getValidationErrors());
            }

            if (iwxxmVersion.isEmpty()) {
                iwxxmVersion = xsdValidationResult.getDetectedIwxxmVersion();
                if (Log.INSTANCE.isDebugEnabled()) {
                    Log.INSTANCE.debug("IWXXM version detected: {}", iwxxmVersion);
                }
            }

            checkIwxxmVersion(iwxxmVersion);
            File[] schematronFiles = getSchematronFiles(iwxxmVersion);
            if (schematronFiles != null && schematronFiles.length > 0) {
                for (File schematronFile : schematronFiles) {
                    if (Log.INSTANCE.isDebugEnabled()) {
                        Log.INSTANCE.debug("Validating against Schematron rules {}", schematronFile.getName());
                    }

                    ValidationResult schematronValidationResult = schematronValidator.validate(iwxxmData, schematronFile.getAbsolutePath());
                    errors.addAll(schematronValidationResult.getValidationErrors());
                }
            }
        } catch (VerifyException e) {
            errors.add(new ValidationError(e.getMessage(), "", 0, 0));
        } catch (Exception e) {
            errors.add(new ValidationError(e.toString(), "", 0, 0));
        }

        return new ValidationResult(iwxxmVersion, errors);
    }

    private static void printValidatingXMLSchema(String catalogFile) {
        if (Log.INSTANCE.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Validating");
            sb.append(" against XML schema");
            if (catalogFile != null && !catalogFile.isEmpty()) {
                sb.append(" using the following catalog: ");
                sb.append(catalogFile);
            }
            Log.INSTANCE.debug(sb.toString());
        }
    }

    private File[] getSchematronFiles(String iwxxmVersion) {
        File file = new File(validationRulesDir, iwxxmVersion);
        return file.listFiles((FileFilter) new WildcardFileFilter("*.sch"));
    }

    private void checkIwxxmVersion(String iwxxmVersion) {
        checkArgument(iwxxmVersion != null && iwxxmVersionPattern.matcher(iwxxmVersion).matches(), "Invalid characters in IWXXM version");
    }
}
