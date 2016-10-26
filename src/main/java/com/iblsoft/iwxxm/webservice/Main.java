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

package com.iblsoft.iwxxm.webservice;

import com.iblsoft.iwxxm.webservice.config.ConfigManager;
import com.iblsoft.iwxxm.webservice.config.Configuration;
import com.iblsoft.iwxxm.webservice.util.Log;
import com.iblsoft.iwxxm.webservice.validator.IwxxmValidator;
import com.iblsoft.iwxxm.webservice.validator.ValidationError;
import com.iblsoft.iwxxm.webservice.validator.ValidationResult;
import com.iblsoft.iwxxm.webservice.ws.ValidationServlet;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.RolloverFileOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TimeZone;

public class Main {

    private static Options commandLineOptions = createCommandLineOptions();
    private static CommandLine commandLine;

    public static void main(String[] args) throws Exception {
        parseCommandLine(args);
        if (commandLine.hasOption("s")) {
            runAsService();
        } else if (commandLine.hasOption("v")) {
            List<String> files = commandLine.getArgList();
            if (files.isEmpty()) {
                printHelp();
            } else {
                validateFiles(files);
            }
        } else {
            printHelp();
        }
    }

    private static void runAsService() throws Exception {
        Configuration configuration = ConfigManager.getConfiguration();

        File logFilePath = new File(configuration.getLogDir(), configuration.getLogFileNamePattern());
        RolloverFileOutputStream os = new RolloverFileOutputStream(logFilePath.getAbsolutePath(), true, configuration.getLogRetainInDays(), TimeZone.getTimeZone("UTC"));
        PrintStream logStream = new PrintStream(os);

        Handler handler = createValidationServletHandler(configuration);
        if (configuration.isAccessLogEnabled()) {
            handler = createAccessLogHandler(configuration, handler);
        }

        printServiceHelp(configuration);

        Server server = new Server(configuration.getServerPort());
        server.setHandler(handler);

        System.setOut(logStream);
        System.setErr(logStream);
        server.start();
        Log.INSTANCE.info("IBL IWXXM Web Service started [{}]", configuration.getServerPort());

        server.join();
    }

    private static void printServiceHelp(Configuration configuration) {
        System.out.println("IWXXM web service started at http://localhost:" + configuration.getServerPort() + configuration.getJsonRpcServiceRootUrl());
        System.out.println("Press Ctrl+C to stop...");
    }

    private static Handler createValidationServletHandler(Configuration configuration) {
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(ValidationServlet.class, configuration.getJsonRpcServiceRootUrl());
        return servletHandler;
    }

    private static RequestLogHandler createAccessLogHandler(Configuration configuration, Handler handler) {
        File logFilePath = new File(configuration.getAccessLogDir(), configuration.getAccessLogFileNamePattern());

        NCSARequestLog requestLog = new NCSARequestLog(logFilePath.toString());
        requestLog.setAppend(true);
        requestLog.setLogLatency(true);
        requestLog.setExtended(configuration.isExtendedAccessLog());
        requestLog.setLogDateFormat(configuration.getAccessLogDateTimeFormat());
        requestLog.setLogTimeZone(configuration.getAccessLogTimeZone());
        requestLog.setPreferProxiedForAddress(configuration.isProxiedAddressPreferredInAccessLog());
        requestLog.setRetainDays(configuration.getAccessLogRetainInDays());

        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        requestLogHandler.setHandler(handler);
        return requestLogHandler;
    }

    private static void validateFiles(List<String> files) {
        Configuration configuration = ConfigManager.getConfiguration();
        IwxxmValidator iwxxmValidator = new IwxxmValidator(configuration.getValidationCatalogFile(), configuration.getValidationRulesDir(), configuration.getDefaultIwxxmVersion());
        for (String filePath : files) {
            filePath = filePath.replace("//", "/");
            File localFile = new File(filePath);
            DirectoryScanner scanner = new DirectoryScanner();
            if (!localFile.isAbsolute()) {
                scanner.setBasedir(".");
            }
            scanner.setIncludes(new String[]{filePath});
            scanner.scan();
            String[] expandedFiles = scanner.getIncludedFiles();
            if (expandedFiles.length == 0) {
                System.out.println("No file matches the pattern " + filePath);
            }
            for (String file : expandedFiles) {
                System.out.print("Validating file " + file + " ... ");
                try {
                    ValidationResult result = iwxxmValidator.validate(FileUtils.readFileToString(new File(file), StandardCharsets.UTF_8.name()), null);
                    if (result.isValid()) {
                        System.out.println("Validation successful");
                    } else {
                        System.out.println("Validation failed");
                        for (ValidationError ve : result.getValidationErrors()) {
                            System.out.println("  " + ve);
                        }
                    }
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Error: " + e.getMessage());
                    System.exit(1);
                    return;
                }
            }
        }
    }

    private static void parseCommandLine(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            commandLine = parser.parse(commandLineOptions, args);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println();
            printHelp();
            System.exit(1);
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("iwxxm [options] [FILEs]\n\noptions:", commandLineOptions);
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption("s", "service", false, "run as json-rpc web service");
        options.addOption("v", "validate", false, "validate provided IWXXM XML files");
        options.addOption("?", "help", false, "show ussage");
        return options;
    }
}
