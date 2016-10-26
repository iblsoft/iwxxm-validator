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

import com.iblsoft.iwxxm.webservice.ws.internal.IwxxmWebServiceImpl;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationRequest;
import com.iblsoft.iwxxm.webservice.ws.messages.ValidationResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IwxxmWebServiceTest
{
    private IwxxmWebService ws;

    @Before
    public void setUp() throws Exception
    {

        this.ws = new IwxxmWebServiceImpl(new File("config/schemas/catalog.xml"), new File("config/schemas/rule"), "2.0");
    }

    @After
    public void tearDown() throws Exception
    {
        this.ws = null;
    }

    @Test
    public void validate_with_valid_messages_should_pass() throws Exception
    {
        String[] iwxxmMessageFileNames = new String[]{
                "iwxxm20-metar-valid.xml",
                "iwxxm20-taf-valid.xml",
                "iwxxm20-sigmet-valid.xml"};

        List<String> testMessages = TestUtils.loadTestResources(iwxxmMessageFileNames);
        for (String iwxxmReport: testMessages) {
            ValidationRequest request = new ValidationRequest();
            request.setRequestVersion("1.0");
            request.setIwxxmData(iwxxmReport);
            ValidationResponse response = this.ws.validate(request);
            assertThat(response.isValid(), is(true));
        }
    }

    @Test
    public void validate_with_invalid_messages_should_report_errors() throws Exception
    {
        String[] iwxxmMessageFileNames = new String[]{"iwxxm20-sigmet-invalid.xml"};

        List<String> testMessages = TestUtils.loadTestResources(iwxxmMessageFileNames);
        for (String iwxxmReport: testMessages) {
            ValidationRequest request = new ValidationRequest();
            request.setRequestVersion("1.0");
            request.setIwxxmData(iwxxmReport);
            ValidationResponse response = this.ws.validate(request);
            assertThat(response.isValid(), is(false));
        }
    }
}