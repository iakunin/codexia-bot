package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import feign.FeignException;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewSenderImpl implements ReviewSender {

    private final CodexiaReviewNotificationRepository codexiaReviewNotificationRepository;

    private final CodexiaClient codexiaClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(CodexiaReview review) {
        final CodexiaReviewNotification savedNotification = this.saveNewNotification(review);
        final ResponseEntity<String> response;
        try {
            response = this.codexiaClient.createReview(
                review.getCodexiaProject().getExternalId(),
                review.getText(),
                review.getUuid().toString()
            );
        } catch (FeignException e) {
            if (e.status() != CodexiaClient.ReviewStatus.ALREADY_EXISTS.httpStatus()) {
                log.warn(
                    "Exception occurred during review creation in Codexia; externalId='{}'",
                    review.getCodexiaProject().getExternalId(),
                    e
                );
            }
            this.saveExceptionalNotification(savedNotification, e);
            return;
        }
        this.saveSuccessfulNotification(savedNotification, response);
    }

    private CodexiaReviewNotification saveNewNotification(CodexiaReview review) {
        return this.codexiaReviewNotificationRepository.save(
            new CodexiaReviewNotification()
                .setCodexiaReview(review)
                .setStatus(CodexiaReviewNotification.Status.NEW)
        );
    }

    private void saveSuccessfulNotification(
        CodexiaReviewNotification notification,
        ResponseEntity<String> response
    ) {
        this.codexiaReviewNotificationRepository.save(
            notification
                .setStatus(CodexiaReviewNotification.Status.SUCCESS)
                .setResponseCode(response.getStatusCodeValue())
                .setResponse(response.toString())
        );
    }

    private void saveExceptionalNotification(
        CodexiaReviewNotification notification,
        FeignException e
    ) {
        this.codexiaReviewNotificationRepository.save(
            notification
                .setStatus(
                    // @todo #19 rewrite via custom Feign exceptions
                    e.status() == CodexiaClient.ReviewStatus.ALREADY_EXISTS.httpStatus()
                        ? CodexiaReviewNotification.Status.SUCCESS
                        : CodexiaReviewNotification.Status.ERROR
                )
                .setResponseCode(e.status())
                .setResponse(
                    new String(
                        e.responseBody()
                            .orElse(ByteBuffer.allocate(0))
                            .array()
                    )
                )
        );
    }
}
