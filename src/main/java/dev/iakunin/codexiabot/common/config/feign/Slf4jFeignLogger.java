package dev.iakunin.codexiabot.common.config.feign;

import feign.Request;
import feign.Response;
import feign.Util;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Slf4jFeignLogger extends feign.Logger {

    private final Logger logger;

    public Slf4jFeignLogger() {
        this(feign.Logger.class);
    }

    public Slf4jFeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public Slf4jFeignLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Not using SLF4J's support for parameterized messages (even though it would be more efficient)
        // because it would require the incoming message formats to be SLF4J-specific.
        logger.info(String.format(methodTag(configKey) + format, args));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        log(configKey, "FEIGN EXTERNAL REQUEST:\n%s", request.toString());
    }

    @Override
    protected Response logAndRebufferResponse(
        String configKey,
        Level logLevel,
        Response response,
        long elapsedTime
    ) throws IOException {
        // rebuild response so that response.toString outputs the actual content
        if (response.body() != null && !(response.status() == 204 || response.status() == 205)) {
            // HTTP 204 No Content "...response MUST NOT include a message-body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            final Response rebuiltResponse = response.toBuilder().body(bodyData).build();
            log(configKey, "FEIGN EXTERNAL RESPONSE:\n%s", rebuiltResponse.toString());

            return response.toBuilder().body(bodyData).build();
        }

        log(configKey, "FEIGN EXTERNAL RESPONSE:\n%s", response.toString());

        return response;
    }

    @Override
    protected IOException logIOException(
        String configKey,
        Level logLevel,
        IOException ioe,
        long elapsedTime
    ) {
        logger.error(methodTag(configKey) + "FEIGN EXTERNAL ERROR:", ioe);

        return ioe;
    }
}
