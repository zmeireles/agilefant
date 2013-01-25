#!/bin/sh

mvn install:install-file -Dfile=flexjson-1.8-custom.jar -DpomFile=flexjson-1.8-custom.pom
mvn install:install-file -Dfile=struts2jsonresult-0.99.jar -DpomFile=struts2jsonresult-0.99.pom
