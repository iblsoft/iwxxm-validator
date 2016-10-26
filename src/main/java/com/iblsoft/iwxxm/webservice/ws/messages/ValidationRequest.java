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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents validation request of JSON RPC web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRequest {

    private String requestVersion;
    private String iwxxmVersion;
    private String iwxxmData;

    public String getRequestVersion() {
        return requestVersion;
    }

    public void setRequestVersion(String requestVersion) {
        this.requestVersion = requestVersion;
    }

    public String getIwxxmVersion() {
        return iwxxmVersion;
    }

    public void setIwxxmVersion(String iwxxmVersion) {
        this.iwxxmVersion = iwxxmVersion;
    }

    public String getIwxxmData() {
        return iwxxmData;
    }

    public void setIwxxmData(String iwxxmData) {
        this.iwxxmData = iwxxmData;
    }
}
