IWXXM Validation Web Service
============================

IWXXM Validation Web Service is a JSON-RPC 2.0 web service which allows to validate 
IWXXM messages against XSD schemas and Schematron rules stored in the local application cache.

It is possible to run application as json-rpc web service or as a command line tool to validate provided files.

Requirements
------------

Requires JDK 1.7 or higher. To build application, Maven 3.2.3 is required.

Building the application
------------------------

To build application use Maven 3.2.3 or later. Go to the directory with pom.xml file and type following command:
```
mvn clean install 
```

Running as a json-rpc web service
---------------------------------

On Linux machine, it is possible to start json-rpc service by command
```
./iwxxm.sh --service
```

On Windows machine, it is possible to start json-rpc service by command
```
iwxxm.cmd --service
```

By default, service is running by default on the port `8040`, on the url `http://<server-name>:8040/json-rpc`.

**Input request:**
* *requestVersion*: version of message request, now must be set to "1.0".
* *iwxxmVersion*: IWXXM version which will be used for validation, if omitted, than version is detected from provided IWXXM data
* *iwxxmData*: content of validated IWXXM XML report or report collection

```json
{
 "jsonrpc": "2.0",
 "id": "iwxxm1",
 "method": "validate",
 "params": {
    "request": {
        "requestVersion": "1.0",
        "iwxxmData": "<content of validates IWXXM XML message>"
    }
  }
}
```

**Output response:**

* *responseVersion*: version of response message, now always 1.0
* *valid*: true or false, indicates if validated message is valid or not
* *validationErrors*: list of validation errors if report or report collection is not valid. If provided iwxxm data are valid, than this property is omitted

Example of response for valid IWXXM data:
```json
{
  "jsonrpc": "2.0",
  "id": "iwxxm1",
  "result": {
    "responseVersion": "1.0",
    "valid": true
  }
}
```

Example of response for invalid IWXXM data:
```json
{
  "jsonrpc": "2.0",
  "id": "iwxxm1",
  "result": {
    "validationErrors": [
    {
      "errorMessage": "cvc-complex-type.4: Attribute 'permissibleUsage' must appear on element 'iwxxm:SIGMET'.",
      "lineNumber": 1,
      "columnNumber": 1108
     },
    {
      "errorMessage": "cvc-complex-type.4: Attribute 'uom' must appear on element 'iwxxm:directionOfMotion'.",
      "lineNumber": 1,
      "columnNumber": 4399
     },
     {
       "errorMessage": "SIGMET.EMC1: directionOfMotion shall be reported in degrees (deg). ((if(exists(iwxxm:directionOfMotion) and (not(exists(iwxxm:directionOfMotion/@xsi:nil)) or iwxxm:directionOfMotion/@xsi:nil != 'true')) then (iwxxm:directionOfMotion/@uom = 'deg') else true()))"
     }
   ],
   "responseVersion": "1.0",
   "valid": false
  }
}
```

**Validation of multiple IWXXM messages in one HTTP request**

It is possible to validate multiple IWXXM messages in one HTTP request. JSON-RPC batching 
can be used for this purpose - add multiple JSON-RPC request in array and send it to the 
web service.

Example of batch request:
```json
[
  {
    "jsonrpc": "2.0",
    "id": "iwxxm1",
    "method": "validate",
    "params": { 
      "request": {
        "requestVersion": "1.0",
        "iwxxmData": "<content of validates IWXXM XML message>"
      }
    }
  },
  {
    "jsonrpc": "2.0",
    "id": "iwxxm2",
    "method": "validate",
    "params": { 
      "request": {
        "requestVersion": "1.0",
        "iwxxmData": "<content of validates IWXXM XML message>"
      }
    }
  }
]
```

Example of batch response:
```json
[
  {
    "jsonrpc": "2.0",
    "id": "iwxxm1",
    "result": {
      "responseVersion": "1.0",
      "valid": true
    }
  },
  {
    "jsonrpc": "2.0",
    "id": "iwxxm2",
    "result": {
      "responseVersion": "1.0",
      "valid": true
    }
  }
]
```

Running validation from command line
------------------------------------

It is possible to validate IWXXM reports stored in local files by following command:
```
./iwxxm.sh --validate file1.xml file2.xml file_with_wildcars.xml
```

or on Windows machine:
```
iwxxm.cmd --validate file1.xml file2.xml file_with_wildcars.xml
```

Updating XML Schema catalog
---------------------------

To prevent download of XSD schemas from the network, XML Schema catalog is stored 
on the local disk in the directory `config/schemas`. Content of XML schema catalog 
(together with schematron rules) is extracted from ZIP file downloaded from 
[http://schemas.wmo.int/iwxxm/2.0/zip/iwxxm-schema-bundle-2.0.zip].

Configuration of web service
----------------------------

Default web service configuration can be changed in the file `config/iwxxm-webservice-config.properties`.
Location of this configuration file can be change by setting JVM property `-DconfigDir=<path_to_directory_with_config_file>`.



