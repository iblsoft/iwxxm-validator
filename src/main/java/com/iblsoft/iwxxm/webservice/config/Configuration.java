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


import org.aeonbits.owner.Config;

import java.io.File;

/**
 * The interface for configuration options loaded from the ${configDir}/iwxxm-webservice-config.properties or
 * ${configDir}/iwxxm-webservice-config.xml file.
 * Dynamic proxy class is automatically generated for this interface by OWNER library.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:${configDir}/iwxxm-webservice-config.xml",
        "file:${configDir}/iwxxm-webservice-config.properties"})
public interface Configuration extends Config {

    @Key("server.port")
    @DefaultValue("8040")
    int getServerPort();

    @Key("server.allow-access-allow-origin")
    @DefaultValue("*")
    String getAccessControlAllowOriginHeader();

    @Key("log.dir")
    @DefaultValue("./logs/")
    File getLogDir();

    @Key("log.filename-pattern")
    @DefaultValue("ibl-vw-iwxxm-webservice.yyyy_mm_dd.log")
    String getLogFileNamePattern();

    @Key("log.retain-in-days")
    @DefaultValue("30")
    int getLogRetainInDays();

    @Key("access-log.enabled")
    @DefaultValue("true")
    boolean isAccessLogEnabled();

    @Key("access-log.dir")
    @DefaultValue("./logs/")
    File getAccessLogDir();

    @Key("access-log.filename-pattern")
    @DefaultValue("ibl-vw-iwxxm-webservice.yyyy_mm_dd.access.log")
    String getAccessLogFileNamePattern();

    @Key("access-log.retain-in-days")
    @DefaultValue("90")
    int getAccessLogRetainInDays();

    @Key("access-log.format.time-zone")
    @DefaultValue("GMT")
    String getAccessLogTimeZone();

    @Key("access-log.format.date-time-format")
    @DefaultValue("yyyy-MM-dd'T'HH:mm:ssZ")
    String getAccessLogDateTimeFormat();

    @Key("access-log.format.extended")
    @DefaultValue("true")
    boolean isExtendedAccessLog();

    @Key("access-log.format.prefer-proxied")
    @DefaultValue("true")
    boolean isProxiedAddressPreferredInAccessLog();

    @Key("iwxxm.default-version")
    @DefaultValue("")
    String getDefaultIwxxmVersion();

    @Key("iwxxm.validation.schemas-dir")
    @DefaultValue("${configDir}/schemas")
    File getValidationSchemasDir();

    @Key("iwxxm.validation.catalog-file")
    @DefaultValue("${iwxxm.validation.schemas-dir}/catalog.xml")
    File getValidationCatalogFile();

    @Key("iwxxm.validation.rules-dir")
    @DefaultValue("${iwxxm.validation.schemas-dir}/rule")
    File getValidationRulesDir();

    @Key("iwxxm.validation.service-url")
    @DefaultValue("/json-rpc")
    String getJsonRpcServiceRootUrl();
}
