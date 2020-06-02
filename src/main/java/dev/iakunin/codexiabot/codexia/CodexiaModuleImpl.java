package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.repository.CodexiaBadgeRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.codexia.service.BadgeSender;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import feign.FeignException;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public final class CodexiaModuleImpl implements CodexiaModule {

    private final CodexiaProjectRepository projects;

    private final CodexiaReviewRepository reviews;

    private final CodexiaClient codexia;

    private final GithubModule github;

    private final CodexiaBadgeRepository badges;

    private final BadgeSender sender;

    @Override
    public void saveReview(final CodexiaReview review) {
        log.debug("Saving a review: {}", review);
        this.reviews.save(review);
    }

    @Override
    public void sendMeta(final CodexiaMeta meta) {
        try {
            this.codexia.setMeta(
                meta.getCodexiaProject().getExternalId(),
                meta.getKey(),
                meta.getValue()
            );
        } catch (final FeignException ex) {
            log.warn(
                "Exception occurred during sending meta to Codexia; externalId='{}'",
                meta.getCodexiaProject().getExternalId(),
                ex
            );
        }
    }

    @Override
    public void applyBadge(final CodexiaBadge badge) {
        this.badges.save(
            this.badges
                .findByCodexiaProjectAndBadge(
                    badge.getCodexiaProject(),
                    badge.getBadge()
                )
                .orElse(badge)
                .setDeletedAt(badge.getDeletedAt())
        );
        this.sender.send(badge);
    }

    @Override
    public Optional<CodexiaProject> findCodexiaProject(final GithubRepo repo) {
        return this.github
            .findAllRepoSources(repo)
            .filter(source -> source.getSource() == GithubModule.Source.CODEXIA)
            .findFirst()
            .flatMap(
                source -> this.projects.findByExternalId(
                    Integer.valueOf(source.getExternalId())
                )
            );
    }

    @Override
    public CodexiaProject getCodexiaProject(final GithubRepo repo) {
        return this.findCodexiaProject(repo)
            .orElseThrow(
                () -> new RuntimeException(
                    String.format(
                        "Unable to find CodexiaProject for githubRepoId='%s'",
                        repo.getId()
                    )
                )
            );
    }

    @Override
    public boolean isReviewExist(
        final CodexiaProject project,
        final String author,
        final String reason
    ) {
        return this.reviews.existsByCodexiaProjectAndAuthorAndReason(project, author, reason);
    }

    @Override
    public Stream<CodexiaReview> findAllReviews(
        final CodexiaProject project,
        final String author
    ) {
        return this.reviews.findAllByCodexiaProjectAndAuthorOrderByIdAsc(project, author);
    }
}
