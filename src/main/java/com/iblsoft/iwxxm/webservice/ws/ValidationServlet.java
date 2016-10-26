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

import com.google.common.net.HttpHeaders;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.iblsoft.iwxxm.webservice.config.ConfigManager;
import com.iblsoft.iwxxm.webservice.config.Configuration;
import com.iblsoft.iwxxm.webservice.ws.internal.IwxxmWebServiceImpl;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Servlet for handling HTTP request of validator web service.
 */
public class ValidationServlet extends HttpServlet {

    private JsonRpcServer jsonRpcServer;
    private String allowOriginHeader;

    @Override
    public void init(ServletConfig config) throws ServletException {
        Configuration configuration = ConfigManager.getConfiguration();
        IwxxmWebService webService = new IwxxmWebServiceImpl(
                configuration.getValidationCatalogFile(),
                configuration.getValidationRulesDir(),
                configuration.getDefaultIwxxmVersion());
        this.allowOriginHeader = nullToEmpty(configuration.getAccessControlAllowOriginHeader());
        this.jsonRpcServer = new JsonRpcServer(webService);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doOptions(request, response);
        addCorsResponseHeaders(response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (InputStream stream = this.getClass().getResourceAsStream("/get-help-response.txt")) {
            IOUtils.copy(stream, response.getWriter());
        }

        response.setContentType("text/plain");
        response.setStatus(200);
        addCorsResponseHeaders(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        addCorsResponseHeaders(response);
        this.jsonRpcServer.handle(request, response);
    }

    private void addCorsResponseHeaders(HttpServletResponse response) {
        if (!this.allowOriginHeader.isEmpty()) {
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, this.allowOriginHeader);
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST,OPTIONS");
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE);
        }
    }
}
