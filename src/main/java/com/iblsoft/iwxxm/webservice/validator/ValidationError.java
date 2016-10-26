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

/**
 * A validation error class which represents the error and the relevant location within the validated file
 */
public class ValidationError {
    private String error;
    private String fileName;
    private Integer lineNumber;
    private Integer columnNumber;

    /**
     * @param error        The error that caused a validation problem
     * @param fileName     The file in which the error occurred
     * @param lineNumber   The line number of the end of the text that
     *                     caused the error or warning
     * @param columnNumber The column number of the end of the text that
     *                     caused the error or warning
     */
    public ValidationError(String error, String fileName, Integer lineNumber, Integer columnNumber) {
        this.error = error;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public String getError() {
        return error;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    @Override
    public String toString() {
        return String.format("line %d, col %d: %s", lineNumber == null ? 0 : lineNumber, columnNumber == null ? 0 : columnNumber, getError());
    }
}
