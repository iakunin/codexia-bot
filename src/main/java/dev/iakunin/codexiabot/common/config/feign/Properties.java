package dev.iakunin.codexiabot.common.config.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @checkstyle MemberName (500 lines)
 * @checkstyle EmptyLineSeparator (500 lines)
 */
@Data
@Component
@ConfigurationProperties("dev.iakunin.codexiabot.logging")
public final class Properties {

    @SuppressWarnings("PMD.LongVariable")
    private String sessionFingerprintHeaderName = "x-session-fingerprint";

    private MdcKeys mdcKeys = new MdcKeys();

    @Data
    public static class MdcKeys {
        private Fingerprint fingerprint = new Fingerprint();
        private Request request = new Request();
        private Response response = new Response();

        @Data
        public static class Fingerprint {
            private String session = "session_fingerprint";
            private String process = "process_fingerprint";
        }

        @Data
        public static class Request {
            private String method = "request_method";
            private String path = "request_path";
            private String body = "request_body";
            private String headers = "request_headers";
        }

        @Data
        public static class Response {
            private String statusCode = "response_status_code";
            private String statusPhrase = "response_status_phrase";
            private String headers = "response_headers";
            private String body = "response_body";
        }
    }
}
