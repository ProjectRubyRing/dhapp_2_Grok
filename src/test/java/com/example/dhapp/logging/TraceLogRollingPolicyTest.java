package com.example.dhapp.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.junit.jupiter.api.Test;

/**
 * tracelog が Log4j2 の RollingFile で、時間単位（yyyy-MM-dd-HH）にローテーションすること。
 */
class TraceLogRollingPolicyTest {

    @Test
    void traceLogRollsOnAnHourBoundaryPattern() throws Exception {
        Path dir = Files.createTempDirectory("tracelog-roll");
        String previous = System.setProperty("EFS_LOGS_PATH", dir.toAbsolutePath().toString().replace('\\', '/'));
        LoggerContext context = new LoggerContext("trace-roll-test");
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("log4j2-spring.xml");
            assertNotNull(url, "log4j2-spring.xml");
            ConfigurationSource source = new ConfigurationSource(url.openStream(), url);
            XmlConfiguration configuration = new XmlConfiguration(context, source);
            context.start(configuration);

            RollingFileAppender appender = configuration.getAppender("TRACE_FILE");
            assertNotNull(appender, "TRACE_FILE");
            assertTrue(appender.getFilePattern().contains("%d{yyyy-MM-dd-HH}"), appender.getFilePattern());
            assertTrue(appender.getFileName().matches(".*[/\\\\]\\d{1,3}-\\d{1,3}-\\d{1,3}-\\d{1,3}_tracelog"),
                    appender.getFileName());

            CompositeTriggeringPolicy composite = assertInstanceOf(
                    CompositeTriggeringPolicy.class, appender.getManager().getTriggeringPolicy());
            TimeBasedTriggeringPolicy policy = null;
            for (TriggeringPolicy candidate : composite.getTriggeringPolicies()) {
                if (candidate instanceof TimeBasedTriggeringPolicy timeBased) {
                    policy = timeBased;
                }
            }
            assertNotNull(policy, composite.toString());
            assertEquals(1, policy.getInterval());
            assertTrue(policy.toString().contains("modulate=true"), policy.toString());

            context.getLogger("com.example.dhapp.controller.HealthCheckController")
                    .info("GET /api/healthcheck");

            Path active = Path.of(appender.getFileName());
            assertTrue(Files.exists(active), active.toString());
            String body = Files.readString(active);
            assertTrue(body.contains("GET /api/healthcheck"), body);

            appender.getManager().rollover();

            try (Stream<Path> files = Files.list(dir)) {
                List<Path> rolled = files
                        .filter(path -> path.getFileName().toString().contains("_tracelog."))
                        .toList();
                assertEquals(1, rolled.size(), rolled.toString());
                assertTrue(rolled.get(0).getFileName().toString().matches(".*_tracelog\\.\\d{4}-\\d{2}-\\d{2}-\\d{2}"),
                        rolled.get(0).toString());
            }
        } finally {
            context.stop();
            if (previous == null) {
                System.clearProperty("EFS_LOGS_PATH");
            } else {
                System.setProperty("EFS_LOGS_PATH", previous);
            }
        }
    }
}
