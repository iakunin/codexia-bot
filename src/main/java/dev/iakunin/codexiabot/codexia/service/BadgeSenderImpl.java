package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public final class BadgeSenderImpl implements BadgeSender {

    private final CodexiaClient codexia;

    @Override
    public void send(final CodexiaBadge badge) {
        if (badge.getDeletedAt() == null) {
            this.attachBadge(badge);
        } else {
            this.detachBadge(badge);
        }
    }

    private void attachBadge(final CodexiaBadge badge) {
        try {
            this.codexia.attachBadge(
                badge.getCodexiaProject().getExternalId(),
                badge.getBadge()
            );
            log.debug(
                "Badge='{}' successfully attach for project='{}'",
                badge.getBadge(),
                badge.getCodexiaProject().getExternalId()
            );
        } catch (final FeignException ex) {
            log.warn(
                "Exception occurred during attaching Codexia badge; externalId='{}'",
                badge.getCodexiaProject().getExternalId(),
                ex
            );
        }
    }

    private void detachBadge(final CodexiaBadge badge) {
        try {
            this.codexia.detachBadge(
                badge.getCodexiaProject().getExternalId(),
                badge.getBadge()
            );
            log.debug(
                "Badge='{}' successfully detached for project='{}'",
                badge.getBadge(),
                badge.getCodexiaProject().getExternalId()
            );
        } catch (final FeignException ex) {
            log.warn(
                "Exception occurred during detaching Codexia badge; externalId='{}'",
                badge.getCodexiaProject().getExternalId(),
                ex
            );
        }
    }
}
