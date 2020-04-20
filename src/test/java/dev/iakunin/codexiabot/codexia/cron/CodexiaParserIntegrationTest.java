package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.util.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = CodexiaParserIntegrationTest.Initializer.class)
public class CodexiaParserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CodexiaParser codexiaParser;

    // @todo #10 Implement CodexiaParserIntegrationTest

    @AfterEach
    void after() {
        WireMockServer.getInstance().resetAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "app.codexia.base-url=" + WireMockServer.getInstance().baseUrl()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
