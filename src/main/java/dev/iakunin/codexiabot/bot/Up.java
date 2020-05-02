package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.bot.up.Bot;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import io.vavr.Tuple2;
import java.util.Deque;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public final class Up implements Runnable {

    private final GithubModule github;

    private final ResultRepository repository;

    private final dev.iakunin.codexiabot.bot.up.Bot bot;

    private final Submitter submitter;

    public Up(
        GithubModule github,
        ResultRepository repository,
        Bot bot,
        CodexiaModule codexiaModule
    ) {
        this.github = github;
        this.repository = repository;
        this.bot = bot;
        this.submitter = new Submitter(bot, repository, codexiaModule);
    }

    public void run() {
        log.debug("Bot type: {}", this.bot.getClass().getName());
        this.github.findAllInCodexia()
            .stream()
            .map(
                repo -> new Tuple2<>(repo, this.getLastProcessedStatId(repo))
            )
            .map(
                pair -> pair.apply(this.github::findAllGithubApiStat)
            )
            .filter(statList -> statList.size() >= 2)
            .filter(statList ->
                statList.getFirst().getStat() != null
                    &&
                statList.getLast().getStat() != null
            )
            .filter(
                statList -> this.bot.shouldSubmit(
                    (GithubApi) statList.getFirst().getStat(),
                    (GithubApi) statList.getLast().getStat()
                )
            )
            .forEach(this.submitter::submit);
    }

    private Long getLastProcessedStatId(GithubRepo repo) {
        return this.repository
            .findFirstByGithubRepoOrderByIdDesc(repo)
            .map(
                result -> result.getGithubRepoStat().getId()
            )
            .orElse(0L);
    }

    @Slf4j
    @AllArgsConstructor
    private static class Submitter {

        private final dev.iakunin.codexiabot.bot.up.Bot bot;

        private final ResultRepository repository;

        private final CodexiaModule codexia;

        // @todo #6 add test case with transaction rollback
        @Transactional
        public void submit(Deque<GithubRepoStat> deque) {
            final CodexiaReview review = this.bot.review(deque.getFirst(), deque.getLast());

            this.repository.save(
                this.bot.result(deque.getLast())
            );
            this.codexia.saveReview(review);
            this.codexia.sendMeta(
                this.bot.meta(review)
            );
        }
    }
}
