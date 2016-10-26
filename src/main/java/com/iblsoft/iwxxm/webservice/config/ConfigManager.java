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

package com.iblsoft.iwxxm.webservice.config;

import com.google.common.collect.Maps;
import org.aeonbits.owner.ConfigFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class which provides access to configuration option saved in configuration file.
 */
public class ConfigManager {

    private static final String PROPERTY_KEY_CONFIG_ROOT_DIR = "configDir";
    private static final String DEFAULT_CONFIG_ROOT_DIR = "./config/";
    private static Map<String, String> globalProperties;

    static {
        init();
    }

    private static String getConfigDirectoryRootPath() {
        Properties systemProperties = System.getProperties();

        String dirName = systemProperties.getProperty(PROPERTY_KEY_CONFIG_ROOT_DIR, "");
        if (dirName.isEmpty()) {
            dirName = DEFAULT_CONFIG_ROOT_DIR;
        }

        if (!dirName.endsWith("/")) {
            dirName += "/";
        }

        return dirName;
    }

    public static Configuration getConfiguration() {
        return ConfigFactory.create(Configuration.class, globalProperties);
    }

    private static void init() {
        Properties systemProperties = System.getProperties();
        Map<String, String> environmentProperties = System.getenv();
        String configRootDir = getConfigDirectoryRootPath();

        globalProperties = new HashMap<>();
        globalProperties.putAll(Maps.fromProperties(systemProperties));
        globalProperties.putAll(environmentProperties);
        globalProperties.put("configDir", configRootDir);

        for (Map.Entry<String, String> entry : globalProperties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ConfigFactory.setProperty(key, value);
        }
    }
}
