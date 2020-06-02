package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    TooManyStarsTransactionIntegrationTest.TestConfig.class
})
public class TooManyStarsTransactionIntegrationTest extends AbstractIntegrationTest {

    private static final String EXCEPTION_MESSAGE = "Some error";

    @Autowired
    private TooManyStars runnable;

    @Test
    @DataSet(
        value = "db-rider/bot/too-many-stars-transaction/initial/transactionRollback.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/too-many-stars-transaction/expected/transactionRollback.yml")
    public void transactionRollback() {
        this.runnable.run();
    }

    @Configuration
    @Import(CodexiaBotApplication.class)
    static class TestConfig {
        @Bean
        @Primary
        public CodexiaModule codexiaModule() {
            final CodexiaModule mock = Mockito.mock(CodexiaModule.class);
            Mockito.doThrow(
                new RuntimeException(
                    TooManyStarsTransactionIntegrationTest.EXCEPTION_MESSAGE
                )
            )
                .when(mock)
                .sendMeta(Mockito.any());

            return mock;
        }
    }
}
