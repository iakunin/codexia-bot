package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeSenderImpl implements BadgeSender {

    private final CodexiaClient codexiaClient;

    @Override
    public void send(CodexiaBadge badge) {
        if (badge.getDeletedAt() == null) {
            this.attachBadge(badge);
        } else {
            this.detachBadge(badge);
        }
    }

    private void attachBadge(CodexiaBadge badge) {
        try {
            this.codexiaClient.attachBadge(
                badge.getCodexiaProject().getExternalId(),
                badge.getBadge()
            );
            log.debug(
                "Badge='{}' successfully attach for project={}",
                badge.getBadge(),
                badge.getCodexiaProject().getExternalId()
            );
        } catch (Exception e) {
            log.warn(
                "Exception occurred during attaching Codexia badge; externalId='{}'",
                badge.getCodexiaProject().getExternalId(),
                e
            );
        }
    }

    private void detachBadge(CodexiaBadge badge) {
        try {
            this.codexiaClient.detachBadge(
                badge.getCodexiaProject().getExternalId(),
                badge.getBadge()
            );
            log.debug(
                "Badge='{}' successfully detached for project={}",
                badge.getBadge(),
                badge.getCodexiaProject().getExternalId()
            );
        } catch (Exception e) {
            log.warn(
                "Exception occurred during detaching Codexia badge; externalId='{}'",
                badge.getCodexiaProject().getExternalId(),
                e
            );
        }
    }
}
