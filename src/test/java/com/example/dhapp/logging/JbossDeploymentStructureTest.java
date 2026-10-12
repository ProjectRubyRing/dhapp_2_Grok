package com.example.dhapp.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * EAP 8.1 / WildFly 35 で war 内の Log4j 2.25.3 が選ばれるための除外が残っていること。
 */
class JbossDeploymentStructureTest {

    @Test
    void excludesServerLog4jApiAndLoggingSubsystem() throws Exception {
        String xml = Files.readString(Path.of("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"));
        assertTrue(xml.contains("<subsystem name=\"logging\"/>"), xml);
        assertTrue(xml.contains("<subsystem name=\"jsf\"/>"), xml);
        assertTrue(xml.contains("<subsystem name=\"weld\"/>"), xml);
        assertTrue(xml.contains("<module name=\"org.apache.logging.log4j.api\"/>"), xml);
        assertTrue(xml.contains("<module name=\"org.jboss.logmanager.log4j2\"/>"), xml);
        assertTrue(xml.contains("<module name=\"org.slf4j\"/>"), xml);
        assertTrue(xml.contains("<module name=\"org.slf4j.impl\"/>"), xml);
    }
}
