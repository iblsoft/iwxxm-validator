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
 ***********************************************************************************************/


package com.iblsoft.iwxxm.webservice.validator;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Holds the result of validation of IWXXM message.
 */
public class ValidationResult {
    private String detectedIwxxmVersion;
    private List<ValidationError> validationErrors;

    public ValidationResult(String detectedIwxxmVersion, List<ValidationError> validationErrors) {
        this.detectedIwxxmVersion = nullToEmpty(detectedIwxxmVersion);
        if (validationErrors == null || validationErrors.isEmpty()) {
            this.validationErrors = Collections.emptyList();
        } else {
            this.validationErrors = Collections.unmodifiableList(validationErrors);
        }
    }

    public boolean isValid() {
        return this.validationErrors.isEmpty();
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public String getDetectedIwxxmVersion() {
        return detectedIwxxmVersion;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + isValid() +
                "validation errors count=" + validationErrors.size() +
                "}";
    }
}
