package dev.iakunin.codexiabot.common.config.feign;

import dev.iakunin.codexiabot.common.config.service.SessionFingerprint;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class SessionFingerprintInterceptor implements RequestInterceptor {

    private final String header;

    private final SessionFingerprint fingerprint;

    @Override
    public void apply(final RequestTemplate template) {
        final Map<String, List<String>> headers = new HashMap<>();

        headers.put(
            this.header,
            Collections.singletonList(
                this.fingerprint.get()
            )
        );

        headers.forEach(template::header);
    }
}
