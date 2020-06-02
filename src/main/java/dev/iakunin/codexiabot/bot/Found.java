package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import io.vavr.Tuple2;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@RequiredArgsConstructor
public class Found implements Runnable {

    private final Bot.Type type;

    private final GithubModule github;

    private final CodexiaModule codexia;

    private final dev.iakunin.codexiabot.bot.found.Bot bot;

    @Transactional
    public void run() {
        log.debug("Bot: {}", this.bot.getClass().getName());
        try (var repos = this.bot.repoStream()) {
            new FaultTolerant(
                repos
                    .flatMap(this::extractAllSources)
                    .filter(
                        pair -> pair.apply(this::shouldSubmit)
                    )
                    .map(
                        pair -> pair.apply(this::createReview)
                    )
                    .map(review -> () -> this.submit(review)),
                tr -> log.error("Unable to submit review", tr.getCause())
            ).run();
        }
    }

    private Stream<Tuple2<CodexiaProject, GithubRepoSource>> extractAllSources(
        final GithubRepo repo
    ) {
        return this.github
            .findAllRepoSources(repo)
            .filter(
                githubRepoSource -> githubRepoSource.getSource() == this.bot.source()
            )
            .map(
                source -> new Tuple2<>(
                    this.codexia.getCodexiaProject(repo),
                    source
                )
            );
    }

    private boolean shouldSubmit(
        final CodexiaProject project,
        final GithubRepoSource source
    ) {
        return !this.codexia.isReviewExist(
            project,
            this.type.name(),
            source.getExternalId()
        );
    }

    private CodexiaReview createReview(
        final CodexiaProject project,
        final GithubRepoSource source
    ) {
        return new CodexiaReview()
            .setText(
                this.bot.reviewText(
                    source.getExternalId()
                )
            )
            .setAuthor(this.type.name())
            .setReason(source.getExternalId())
            .setCodexiaProject(project);
    }

    private void submit(final CodexiaReview review) {
        this.codexia.saveReview(review);
        this.codexia.sendMeta(
            this.bot.meta(review)
        );
    }
}
