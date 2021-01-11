package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.ResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import io.vavr.Tuple2;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@RequiredArgsConstructor
public class Up implements Runnable {

    private final GithubModule github;

    private final ResultRepository repository;

    private final dev.iakunin.codexiabot.bot.up.Bot bot;

    private final Submitter submitter;

    @Transactional
    public void run() {
        log.debug("Bot type: {}", this.bot.getClass().getName());
        try (var repos = this.github.findAllInCodexia()) {
            new FaultTolerant(
                repos
                    .map(repo -> new Tuple2<>(repo, this.getLastProcessedStatId(repo)))
                    .map(pair -> pair.apply(this.github::findAllGithubApiStat))
                    .map(stream -> stream.collect(Collectors.toCollection(LinkedList::new)))
                    .filter(statList -> statList.size() >= 2)
                    .filter(statList ->
                        statList.getFirst().getStat() != null
                        && statList.getLast().getStat() != null
                    )
                    .filter(statList ->
                        this.bot.shouldSubmit(
                            (GithubApi) statList.getFirst().getStat(),
                            (GithubApi) statList.getLast().getStat()
                        )
                    )
                    .map(stat -> () -> this.submitter.submit(stat)),
                tr -> log.error("Unable to submit review", tr.getCause())
            ).run();
        }
    }

    private Long getLastProcessedStatId(final GithubRepo repo) {
        return this.repository
            .findFirstByGithubRepoOrderByIdDesc(repo)
            .map(
                result -> result.getGithubRepoStat().getId()
            )
            .orElse(0L);
    }

    @RequiredArgsConstructor
    public static class Submitter {

        private final dev.iakunin.codexiabot.bot.up.Bot bot;

        private final ResultRepository repository;

        private final CodexiaModule codexia;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void submit(final Deque<GithubRepoStat> deque) {
            final CodexiaReview review = this.bot.review(deque.getFirst(), deque.getLast());
            this.repository.save(this.bot.result(deque.getLast()));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.bot.meta(review));
        }
    }
}
