package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import feign.FeignException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class CodexiaModuleImpl implements CodexiaModule {

    private static final int REVIEW_ALREADY_EXISTS_STATUS = 404;

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

        this.setMetaForHackerNewsTmp(review); //@TODO: remove me!

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
                    .setStatus(
                        e.status() == REVIEW_ALREADY_EXISTS_STATUS
                            ? CodexiaReviewNotification.Status.SUCCESS
                            : CodexiaReviewNotification.Status.ERROR
                    )
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
    public void sendMeta(CodexiaProject codexiaProject, String metaKey, String metaValue) {
        try {
            this.codexiaClient.setMeta(
                String.valueOf(codexiaProject.getExternalId()),
                metaKey,
                metaValue
            );
        } catch (Exception e) {
            log.warn("Exception occurred during sending meta to Codexia", e);
        }
    }

    private void setMetaForHackerNewsTmp(CodexiaReview review) {
        this.sendMeta(
            review.getCodexiaProject(),
            "hacker-news-id",
            this.codexiaReviewRepository
                .findAllByCodexiaProjectAndAuthor(
                    review.getCodexiaProject(),
                    review.getAuthor()
                )
                .stream()
                .map(
                    CodexiaReview::getReason
                ).collect(Collectors.joining(","))
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

    @Override
    public List<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author) {
        return this.codexiaReviewRepository.findAllByCodexiaProjectAndAuthor(codexiaProject, author);
    }
}
