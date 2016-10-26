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

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Holds information about validation errors in JSON RPC validation response.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationError {

    private final String errorMessage;
    private final Integer lineNumber;
    private final Integer columnNumber;

    public ValidationError(String errorMessage, Integer lineNumber, Integer columnNumber) {
        this.errorMessage = nullToEmpty(errorMessage);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    @Override
    public String toString() {
        if (lineNumber == null && columnNumber == null) {
            return getErrorMessage();
        } else {
            return String.format("%s at line %d, column %d", errorMessage, lineNumber, columnNumber);
        }
    }
}
