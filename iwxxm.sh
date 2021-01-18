#!/bin/bash

JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
java $JAVA_OPTS -jar bin/ibl-iwxxm-utils.jar "$@"
