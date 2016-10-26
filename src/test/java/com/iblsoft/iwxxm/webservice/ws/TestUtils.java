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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TestUtils {
    public static List<String> loadTestResources(String[] resourcePaths) throws Exception {
        List<String> messages = new ArrayList<>(resourcePaths.length);
        for (String resourceName: resourcePaths) {
            messages.add(loadTestResource(resourceName));
        }
        return Collections.unmodifiableList(messages);
    }

    public static String loadTestResource(String resourcePath) throws IOException
    {
        try (InputStream stream = TestUtils.class.getResourceAsStream("/iwxxm-messages/" + resourcePath))
        {
            return IOUtils.toString(stream);
        }
    }
}
