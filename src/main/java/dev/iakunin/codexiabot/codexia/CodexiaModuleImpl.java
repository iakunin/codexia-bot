package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReviewNotification;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewNotificationRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import feign.FeignException;
import java.util.List;
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

    private static final int REVIEW_ALREADY_EXISTS_STATUS = 404;

    private final CodexiaProjectRepository codexiaProjectRepository;

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final CodexiaReviewNotificationRepository codexiaReviewNotificationRepository;

    private final CodexiaClient codexiaClient;

    private final GithubModule githubModule;

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
                savedReview.getCodexiaProject().getExternalId(),
                savedReview.getText(),
                savedReview.getUuid().toString()
            );
        } catch (FeignException e) {
            log.warn(
                "Exception occurred during review creation in Codexia; externalId='{}'",
                review.getCodexiaProject().getExternalId(),
                e
            );
            this.codexiaReviewNotificationRepository.save(
                savedNotification
                    .setStatus(
                        e.status() == REVIEW_ALREADY_EXISTS_STATUS
                            ? CodexiaReviewNotification.Status.SUCCESS
                            : CodexiaReviewNotification.Status.ERROR
                    )
                    .setResponseCode(e.status())
                    .setResponse(e.content() != null ? e.contentUTF8() : "")
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
                codexiaProject.getExternalId(),
                metaKey,
                metaValue
            );
        } catch (Exception e) {
            log.warn(
                "Exception occurred during sending meta to Codexia; externalId='{}'",
                codexiaProject.getExternalId(),
                e
            );
        }
    }

    @Override
    public Optional<CodexiaProject> findCodexiaProject(GithubRepo repo) {
        return this.githubModule
            .findAllRepoSources(repo)
            .stream()
            .filter(source -> source.getSource() == GithubModule.Source.CODEXIA)
            .findFirst()
            .flatMap(
                source -> this.codexiaProjectRepository.findByExternalId(
                    Integer.valueOf(source.getExternalId())
                )
            );
    }

    @Override
    public boolean isReviewExist(CodexiaProject codexiaProject, String author, String reason) {
        return this.codexiaReviewRepository.existsByCodexiaProjectAndAuthorAndReason(codexiaProject, author, reason);
    }

    @Override
    public List<CodexiaReview> findAllReviews(CodexiaProject codexiaProject, String author) {
        return this.codexiaReviewRepository.findAllByCodexiaProjectAndAuthorOrderByIdAsc(codexiaProject, author);
    }
}
