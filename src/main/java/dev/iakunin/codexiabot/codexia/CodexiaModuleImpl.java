package dev.iakunin.codexiabot.codexia;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class CodexiaModuleImpl implements CodexiaModule {

    private final CodexiaProjectRepository codexiaProjectRepository;

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final CodexiaClient codexiaClient;

    private final GithubModule githubModule;

    @Override
    public void saveReview(CodexiaReview review) {
        log.info("Saving a review: {}", review);
        this.codexiaReviewRepository.save(review);
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
