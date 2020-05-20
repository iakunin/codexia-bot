package dev.iakunin.codexiabot.common.config.feign;

import dev.iakunin.codexiabot.common.config.service.SessionFingerprint;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class SessionFingerprintInterceptor implements RequestInterceptor {

    private final String sessionFingerprintHeaderName;
    private final SessionFingerprint sessionFingerprint;

    @Override
    public void apply(RequestTemplate template) {
        TreeMap<String, List<String>> headers = new TreeMap<>();

        headers.put(
            this.sessionFingerprintHeaderName,
            Collections.singletonList(
                this.sessionFingerprint.get()
            )
        );

        headers.forEach(template::header);
    }
}
