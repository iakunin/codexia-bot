package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.bot.up.Bot;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.util.Deque;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public final class Up implements Runnable {

    private final GithubModule githubModule;

    private final ResultRepository resultRepository;

    private final dev.iakunin.codexiabot.bot.up.Bot upBot;

    private final Processor processor;

    public Up(
        GithubModule githubModule,
        ResultRepository resultRepository,
        Bot upBot,
        CodexiaModule codexiaModule
    ) {
        this.githubModule = githubModule;
        this.resultRepository = resultRepository;
        this.upBot = upBot;
        this.processor = new Processor(upBot, resultRepository, codexiaModule);
    }

    public void run() {
        log.info("Bot type: {}", upBot.getClass().getName());
        this.githubModule.findAllInCodexia()
            .stream()
            .map(
                repo -> Pair.with(repo, this.getLastProcessedStatId(repo))
            )
            .map(
                pair -> this.githubModule.findAllGithubApiStat(
                    pair.getValue0(),
                    pair.getValue1()
                )
            )
            .filter(statList -> statList.size() >= 2)
            .filter(
                statList -> this.upBot.shouldReviewBeSubmitted(
                    (GithubApi) statList.getFirst().getStat(),
                    (GithubApi) statList.getLast().getStat()
                )
            )
            .forEach(this.processor::process);
    }

    private Long getLastProcessedStatId(GithubRepo repo) {
        return this.resultRepository
            .findFirstByGithubRepoOrderByIdDesc(repo)
            .map(
                result -> result.getGithubRepoStat().getId()
            )
            .orElse(0L);
    }

    @Slf4j
    @AllArgsConstructor(onConstructor_={@Autowired})
    private static class Processor {

        private final dev.iakunin.codexiabot.bot.up.Bot upBot;

        private final ResultRepository resultRepository;

        private final CodexiaModule codexiaModule;

        // @todo #6 add test case with transaction rollback
        @Transactional
        public void process(Deque<GithubRepoStat> deque) {
            final CodexiaReview review = this.upBot.createReview(deque.getFirst(), deque.getLast());

            this.resultRepository.save(
                this.upBot.createResult(deque.getLast())
            );
            this.codexiaModule.saveReview(review);
            this.codexiaModule.sendMeta(
                this.upBot.createMeta(review)
            );
        }
    }
}
