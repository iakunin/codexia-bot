package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Found implements Runnable {

    private final Bot.Type botType;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final dev.iakunin.codexiabot.bot.found.Bot bot;

    public void run() {
        log.info("Bot: {}", this.bot.getClass().getName());
        this.bot
            .repoStream()
            .flatMap(this::extractAllSources)
            .filter(this::shouldSubmit)
            .map(this::createReview)
            .forEach(this::submit);
    }

    private Stream<Pair<CodexiaProject, GithubRepoSource>> extractAllSources(GithubRepo githubRepo) {
        return this.githubModule
            .findAllRepoSources(githubRepo)
            .stream()
            .filter(
                githubRepoSource -> githubRepoSource.getSource() == this.bot.source()
            )
            .map(
                source -> new Pair<>(
                    this.codexiaModule.getCodexiaProject(githubRepo),
                    source
                )
            );
    }

    private boolean shouldSubmit(Pair<CodexiaProject, GithubRepoSource> dto) {
        return !this.codexiaModule.isReviewExist(
            dto.getValue0(),
            this.botType.name(),
            dto.getValue1().getExternalId()
        );
    }

    private CodexiaReview createReview(Pair<CodexiaProject, GithubRepoSource> pair) {
        return new CodexiaReview()
            .setText(
                this.bot.reviewText(
                    pair.getValue1().getExternalId()
                )
            )
            .setAuthor(this.botType.name())
            .setReason(pair.getValue1().getExternalId())
            .setCodexiaProject(pair.getValue0());
    }

    private void submit(CodexiaReview review) {
        this.codexiaModule.saveReview(review);
        this.codexiaModule.sendMeta(
            this.bot.meta(review)
        );
    }
}
