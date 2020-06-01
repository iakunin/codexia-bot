package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.bot.toosmall.ExactItem;
import dev.iakunin.codexiabot.bot.toosmall.LogNotFound;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import io.vavr.Tuple2;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.scalar.Unchecked;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Slf4j
@RequiredArgsConstructor
public class Small implements Runnable {

    private final GithubModule github;

    private final dev.iakunin.codexiabot.bot.toosmall.Bot bot;

    private final Submitter submitter;

    @Transactional
    public void run() {
        try (var repos = this.bot.repoStream()) {
            new FaultTolerant(
                repos
                    .map(this::prepare)
                    .map(
                        optional -> () -> optional
                            .filter(pair -> this.bot.shouldSubmit(pair._2()))
                            .ifPresent(pair -> pair.apply(this.submitter::submit))
                    ),
                tr -> log.error("Unable to submit review", tr.getCause())
            ).run();
        }
    }

    private Optional<Tuple2<GithubRepoStat, Item>> prepare(final GithubRepo repo) {
        return
            new Tuple2<>(
                this.github.findLastGithubApiStat(repo),
                this.github.findLastLinesOfCodeStat(repo)
            ).apply(
                (fst, snd) -> fst.isPresent() && snd.isPresent()
                    ? Optional.of(new Tuple2<>(fst.get(), snd.get()))
                    : Optional.<Tuple2<GithubRepoStat, GithubRepoStat>>empty()
            ).flatMap(
                o -> o.apply(this::findLinesOfCodeItem)
            ).flatMap(linesOfCodeItem ->
                this.github.findLastLinesOfCodeStat(repo)
                    .map(stat -> new Tuple2<>(stat, linesOfCodeItem))
            );
    }

    private Optional<Item> findLinesOfCodeItem(
        final GithubRepoStat githb,
        final GithubRepoStat loc
    ) {
        return
            new Unchecked<>(
                new LogNotFound(
                    githb,
                    loc,
                    new ExactItem(
                        (GithubApi) githb.getStat(),
                        (LinesOfCode) loc.getStat()
                    )
                )
            ).value();
    }

    @RequiredArgsConstructor
    public static class Submitter {

        private final dev.iakunin.codexiabot.bot.toosmall.Bot bot;

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public Void submit(final GithubRepoStat stat, final Item item) {
            final CodexiaReview review = this.bot.review(stat, item);
            this.repository.save(this.bot.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.bot.meta(review));
            this.bot.badge(review.getCodexiaProject());
            return null;
        }
    }
}

