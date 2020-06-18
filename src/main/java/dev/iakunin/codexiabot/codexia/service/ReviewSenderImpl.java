package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification.Status;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import feign.FeignException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewSenderImpl implements ReviewSender {

    private final CodexiaReviewNotificationRepository repository;

    private final CodexiaClient client;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(final CodexiaReview review) {
        final CodexiaReviewNotification saved = this.saveNewNotification(review);
        final ResponseEntity<String> response;
        try {
            response = this.client.createReview(
                review.getCodexiaProject().getExternalId(),
                review.getText(),
                review.getUuid().toString()
            );
        } catch (final FeignException ex) {
            if (ex.status() != CodexiaClient.ReviewStatus.ALREADY_EXISTS.httpStatus()) {
                log.warn(
                    "Exception occurred during review creation in Codexia; externalId='{}'",
                    review.getCodexiaProject().getExternalId(),
                    ex
                );
            }
            this.saveExceptionalNotification(saved, ex);
            return;
        }
        this.saveSuccessfulNotification(saved, response);
    }

    private CodexiaReviewNotification saveNewNotification(final CodexiaReview review) {
        return this.repository.save(
            new CodexiaReviewNotification()
                .setCodexiaReview(review)
                .setStatus(Status.NEW)
        );
    }

    private void saveSuccessfulNotification(
        final CodexiaReviewNotification notification,
        final ResponseEntity<String> response
    ) {
        this.repository.save(
            notification
                .setStatus(Status.SUCCESS)
                .setResponseCode(response.getStatusCodeValue())
                .setResponse(response.toString())
        );
    }

    private void saveExceptionalNotification(
        final CodexiaReviewNotification notification,
        final FeignException exception
    ) {
        this.repository.save(
            notification
                .setStatus(
                    // @todo #19 rewrite via custom Feign exceptions
                    exception.status() == CodexiaClient.ReviewStatus.ALREADY_EXISTS.httpStatus()
                        ? Status.SUCCESS
                        : Status.ERROR
                )
                .setResponseCode(exception.status())
                .setResponse(
                    new String(
                        exception.responseBody()
                            .orElse(ByteBuffer.allocate(0))
                            .array(),
                        StandardCharsets.UTF_8
                    )
                )
        );
    }
}
