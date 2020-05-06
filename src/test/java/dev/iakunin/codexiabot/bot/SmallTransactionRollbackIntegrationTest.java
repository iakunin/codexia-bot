package dev.iakunin.codexiabot.bot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import dev.iakunin.codexiabot.AbstractIntegrationTest;
import dev.iakunin.codexiabot.CodexiaBotApplication;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest(classes = {
    AbstractIntegrationTest.TestConfig.class,
    SmallTransactionRollbackIntegrationTest.TestConfig.class,
})
public class SmallTransactionRollbackIntegrationTest extends AbstractIntegrationTest {

    private static final String EXCEPTION_MESSAGE = "Some error";

    @Qualifier("tooSmall")
    @Autowired
    private Small tooSmall;

    @Test
    @DataSet(
        value = "db-rider/bot/small-transaction-rollback/initial/transactionRollback.yml",
        cleanBefore = true, cleanAfter = true
    )
    @ExpectedDataSet("db-rider/bot/small-transaction-rollback/expected/transactionRollback.yml")
    public void transactionRollback() {
        final RuntimeException exception = assertThrows(RuntimeException.class, this.tooSmall::run);

        assertEquals(
            exception.getMessage(),
            SmallTransactionRollbackIntegrationTest.EXCEPTION_MESSAGE
        );
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
                    SmallTransactionRollbackIntegrationTest.EXCEPTION_MESSAGE
                )
            )
                .when(mock)
                .sendMeta(Mockito.any());

            return mock;
        }
    }
}
