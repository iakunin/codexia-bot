package dev.iakunin.codexiabot.common.config.feign;

import feign.Request;
import feign.Response;
import feign.Util;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @checkstyle ParameterNumber (500 lines)
 * @checkstyle ReturnCount (500 lines)
 */
final class Slf4jFeignLogger extends feign.Logger {

    private static final int NO_CONTENT = 204;

    private static final int RESET_CONTENT = 205;

    private final Logger logger;

    Slf4jFeignLogger() {
        this(feign.Logger.class);
    }

    Slf4jFeignLogger(final Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    Slf4jFeignLogger(final Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    protected void log(final String key, final String format, final Object... args) {
        // Not using SLF4J's support for parameterized messages
        // (even though it would be more efficient)
        // because it would require the incoming message formats to be SLF4J-specific.
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format(methodTag(key) + format, args));
        }
    }

    @Override
    protected void logRequest(final String key, final Level level, final Request request) {
        this.log(key, "FEIGN EXTERNAL REQUEST:\n%s", request.toString());
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    protected Response logAndRebufferResponse(
        final String key,
        final Level level,
        final Response response,
        final long time
    ) throws IOException {
        // rebuild response so that response.toString outputs the actual content
        if (response.body() != null
            // HTTP 204 No Content "...response MUST NOT include a message-body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            && !(response.status() == NO_CONTENT || response.status() == RESET_CONTENT)
        ) {
            final byte[] body = Util.toByteArray(response.body().asInputStream());
            final Response rebuilt = response.toBuilder().body(body).build();
            this.logExternalResponse(key, rebuilt);

            return response.toBuilder().body(body).build();
        }

        this.logExternalResponse(key, response);

        return response;
    }

    @Override
    protected IOException logIOException(
        final String key,
        final Level level,
        final IOException ioe,
        final long time
    ) {
        if (this.logger.isErrorEnabled()) {
            this.logger.error(methodTag(key) + "FEIGN EXTERNAL ERROR:", ioe);
        }

        return ioe;
    }

    private void logExternalResponse(final String key, final Response response) {
        this.log(key, "FEIGN EXTERNAL RESPONSE:\n%s", response.toString());
    }
}
