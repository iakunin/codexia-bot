package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.bot.toosmall.ExactItem;
import dev.iakunin.codexiabot.bot.toosmall.LogNotFound;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import io.vavr.Tuple2;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cactoos.scalar.Unchecked;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class Small implements Runnable {

    private final GithubModule github;

    private final dev.iakunin.codexiabot.bot.toosmall.Bot bot;

    private final Submitter submitter;

    @Transactional
    public void run() {
        this.bot
            .repoStream()
            .map(this::prepare)
            .forEach(
                optional -> optional
                    .filter(pair -> this.bot.shouldSubmit(pair._2()))
                    .ifPresent(pair -> pair.apply(this.submitter::submit))
            )
        ;
    }

    private Optional<Tuple2<GithubRepoStat, Item>> prepare(GithubRepo repo) {
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
        GithubRepoStat github,
        GithubRepoStat linesOfCode
    ) {
        return
            new Unchecked<>(
                new LogNotFound(
                    github,
                    linesOfCode,
                    new ExactItem(
                        (GithubApi) github.getStat(),
                        (LinesOfCode) linesOfCode.getStat()
                    )
                )
            ).value();
    }

    @AllArgsConstructor
    public static class Submitter {

        private final dev.iakunin.codexiabot.bot.toosmall.Bot bot;

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public Void submit(GithubRepoStat stat, Item item) {
            final CodexiaReview review = this.bot.review(stat, item);
            this.repository.save(this.bot.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.bot.meta(review));
            return null;
        }
    }
}

