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


package com.iblsoft.iwxxm.webservice.ws;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationRequest;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationResponse;

/**
 * Interface of JSON RPC web service.
 */
public interface IwxxmWebService {
    /**
     * Validates IWXXM message in the request.
     * @param request Data to be validated.
     * @return Result of message validation.
     */
    ValidationResponse validate(@JsonRpcParam("request") ValidationRequest request);
}
