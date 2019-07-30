#!/usr/bin/env bash
MAVEN_LOCAL_REPO=$(cd / && mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
TOOL_GROUP_ID=io.choerodon
TOOL_ARTIFACT_ID=choerodon-tool-liquibase
TOOL_VERSION=0.11.1.RELEASE
TOOL_JAR_PATH=${MAVEN_LOCAL_REPO}/${TOOL_GROUP_ID/\./\/}/${TOOL_ARTIFACT_ID}/${TOOL_VERSION}/${TOOL_ARTIFACT_ID}-${TOOL_VERSION}.jar
mvn org.apache.maven.plugins:maven-dependency-plugin:get \
 -Dartifact=${TOOL_GROUP_ID}:${TOOL_ARTIFACT_ID}:${TOOL_VERSION} \
 -Dtransitive=false

cd hap-core
mvn clean package spring-boot:repackage
cd ..
java -Dspring.datasource.url="jdbc:mysql://localhost/hap_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
 -Dspring.datasource.username=hap_dev \
 -Dspring.datasource.password=hap_dev \
 -Ddata.drop=false -Ddata.mode=all \
 -Ddata.jar=hap-core/target/app.jar \
 -jar ${TOOL_JAR_PATH}