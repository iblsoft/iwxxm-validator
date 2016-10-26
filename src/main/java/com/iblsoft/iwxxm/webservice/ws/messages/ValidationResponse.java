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

package com.iblsoft.iwxxm.webservice.ws.messages;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents validation response of JSON RPC web service.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationResponse {

    private List<ValidationError> validationErrors;

    private ValidationResponse(Builder builder) {
        this.validationErrors = Collections.unmodifiableList(builder.validationErrors);
    }

    public String getResponseVersion() {
        return "1.0";
    }

    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    public List<ValidationError> getValidationErrors() {
        return this.validationErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ValidationResponse {" +
                "valid=" + isValid() +
                "validation errors count=" + validationErrors.size() +
                "}";
    }

    /**
     * Helper builder class.
     */
    public static class Builder {
        private List<ValidationError> validationErrors = new ArrayList<>(4);

        private Builder() {
        }

        public Builder addValidationError(String errorMessage, Integer lineNumber, Integer columnNumber) {
            this.validationErrors.add(new ValidationError(errorMessage, lineNumber, columnNumber));
            return this;
        }

        public ValidationResponse build() {
            return new ValidationResponse(this);
        }
    }
}
