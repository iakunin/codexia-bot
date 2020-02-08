package com.iakunin.codexiabot.common.config.service;

import com.iakunin.codexiabot.common.config.feign.Properties;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public final class SessionFingerprintImpl implements SessionFingerprint {

    private final String key;

    public SessionFingerprintImpl(Properties properties) {
        this.key = properties.getMdcKeys().getFingerprint().getSession();
    }

    @Override
    public String get() {
        return MDC.get(this.key);
    }

    @Override
    public void set(String sessionFingerprint) {
        MDC.put(this.key, sessionFingerprint);
    }

    @Override
    public void unset() {
        MDC.remove(this.key);
    }
}
