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


package com.iblsoft.iwxxm.webservice.util;

import org.slf4j.LoggerFactory;

/**
 * Utility class for logging information, warning or debug messages into log file.
 */
public enum Log {
    INSTANCE;

    private org.slf4j.Logger logger = LoggerFactory.getLogger("com.iblsoft.vw.iwxxm.webservice");

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
}
