package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import feign.FeignException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class CodexiaModuleImpl implements CodexiaModule {

    private final CodexiaProjectRepository codexiaProjectRepository;

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final CodexiaReviewNotificationRepository codexiaReviewNotificationRepository;

    private final CodexiaClient codexiaClient;

    @Override
    public void sendReview(CodexiaReview review) {
        log.info("Got a review: {}", review);

        final CodexiaReview savedReview = this.codexiaReviewRepository.save(review);

        final CodexiaReviewNotification savedNotification = this.codexiaReviewNotificationRepository.save(
            new CodexiaReviewNotification()
                .setCodexiaReview(savedReview)
                .setStatus(CodexiaReviewNotification.Status.NEW)
        );

        final ResponseEntity<String> response;
        try {
            response = this.codexiaClient.createReview(
                String.valueOf(savedReview.getCodexiaProject().getExternalId()),
                savedReview.getText(),
                savedReview.getUuid().toString()
            );
        } catch (FeignException e) {
            log.warn("Exception occurred during review creation in Codexia", e);
            this.codexiaReviewNotificationRepository.save(
                savedNotification
                    .setStatus(CodexiaReviewNotification.Status.ERROR)
                    .setResponseCode(e.status())
                    .setResponse(e.contentUTF8())
            );
            return;
        }

        this.codexiaReviewNotificationRepository.save(
            savedNotification
                .setStatus(CodexiaReviewNotification.Status.SUCCESS)
                .setResponseCode(response.getStatusCodeValue())
                .setResponse(response.toString())
        );
    }

    @Override
    public Optional<CodexiaProject> findByExternalId(Integer externalId) {
        return this.codexiaProjectRepository.findByExternalId(externalId);
    }

    @Override
    public Boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason) {
        return this.codexiaReviewRepository.existsByCodexiaProjectAndAuthorAndReason(codexiaProject, author, reason);
    }
}
