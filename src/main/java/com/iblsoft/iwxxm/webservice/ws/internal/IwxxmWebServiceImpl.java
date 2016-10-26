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

package com.iblsoft.iwxxm.webservice.ws.internal;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.iblsoft.iwxxm.webservice.util.Log;
import com.iblsoft.iwxxm.webservice.validator.IwxxmValidator;
import com.iblsoft.iwxxm.webservice.validator.ValidationError;
import com.iblsoft.iwxxm.webservice.validator.ValidationResult;
import com.iblsoft.iwxxm.webservice.ws.IwxxmWebService;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationRequest;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationResponse;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation class of IwxxmWebService interface.
 */
public class IwxxmWebServiceImpl implements IwxxmWebService {

    private final IwxxmValidator iwxxmValidator;

    public IwxxmWebServiceImpl(File validationCatalogFile, File validationRulesDir, String defaultIwxxmVersion) {
        this.iwxxmValidator = new IwxxmValidator(validationCatalogFile, validationRulesDir, defaultIwxxmVersion);
    }

    @Override
    public ValidationResponse validate(@JsonRpcParam("request") ValidationRequest request) {
        Log.INSTANCE.debug("IwxxmWebService.validate request started");

        checkRequestVersion(request.getRequestVersion());

        ValidationResult validationResult = iwxxmValidator.validate(request.getIwxxmData(), request.getIwxxmVersion());
        ValidationResponse.Builder responseBuilder = ValidationResponse.builder();
        for (ValidationError ve : validationResult.getValidationErrors()) {
            responseBuilder.addValidationError(ve.getError(), ve.getLineNumber(), ve.getColumnNumber());
        }

        Log.INSTANCE.debug("IwxxmWebService.validate request finished");
        return responseBuilder.build();
    }

    private void checkRequestVersion(String requestVersion) {
        checkArgument(requestVersion != null && requestVersion.equals("1.0"), "Unsupported request version.");
    }
}
