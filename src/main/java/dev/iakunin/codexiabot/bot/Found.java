package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import io.vavr.Tuple2;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class Found implements Runnable {

    private final Bot.Type botType;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final dev.iakunin.codexiabot.bot.found.Bot bot;

    @Transactional
    public void run() {
        log.debug("Bot: {}", this.bot.getClass().getName());
        this.bot
            .repoStream()
            .flatMap(this::extractAllSources)
            .filter(
                pair -> pair.apply(this::shouldSubmit)
            )
            .map(
                pair -> pair.apply(this::createReview)
            )
            .forEach(this::submit);
    }

    private Stream<Tuple2<CodexiaProject, GithubRepoSource>> extractAllSources(GithubRepo githubRepo) {
        return this.githubModule
            .findAllRepoSources(githubRepo)
            .filter(
                githubRepoSource -> githubRepoSource.getSource() == this.bot.source()
            )
            .map(
                source -> new Tuple2<>(
                    this.codexiaModule.getCodexiaProject(githubRepo),
                    source
                )
            );
    }

    private boolean shouldSubmit(
        CodexiaProject project,
        GithubRepoSource source
    ) {
        return !this.codexiaModule.isReviewExist(
            project,
            this.botType.name(),
            source.getExternalId()
        );
    }

    private CodexiaReview createReview(
        CodexiaProject project,
        GithubRepoSource source
    ) {
        return new CodexiaReview()
            .setText(
                this.bot.reviewText(
                    source.getExternalId()
                )
            )
            .setAuthor(this.botType.name())
            .setReason(source.getExternalId())
            .setCodexiaProject(project);
    }

    private void submit(CodexiaReview review) {
        this.codexiaModule.saveReview(review);
        this.codexiaModule.sendMeta(
            this.bot.meta(review)
        );
    }
}
