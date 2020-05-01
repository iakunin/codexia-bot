package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.bot.toosmall.ExactItem;
import dev.iakunin.codexiabot.bot.toosmall.LogNotFound;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public final class NotSmallAnymore implements Runnable {

    private final GithubModule github;

    private final Submitter submitter;

    private final TooSmallResultRepository repository;

    public NotSmallAnymore(
        GithubModule github,
        CodexiaModule codexia,
        TooSmallResultRepository repository
    ) {
        this.github = github;
        this.repository = repository;
        this.submitter = new Submitter(codexia, repository);
    }

    public void run() {
        this.github.findAllInCodexia()
            .stream()
            .filter(repo -> {
                final Optional<TooSmallResult> optional = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);
                return optional.isPresent() && optional.get().getState() == TooSmallResult.State.SET;
            })
            .map(this::prepare)
            .forEach(
                optional -> optional
                    .filter(pair -> this.shouldSubmit(pair._2()))
                    .ifPresent(pair -> pair.apply(this.submitter::submit))
            )
        ;
    }

    private Optional<Tuple2<GithubRepoStat, Item>> prepare(GithubRepo repo) {
        return
            new Tuple2<>(
                this.github.findLastGithubApiStat(repo),
                this.github.findLastLinesOfCodeStat(repo)
            ).map(
                o -> o.map(stat -> (GithubApi) stat.getStat()),
                o -> o.map(stat -> (LinesOfCode) stat.getStat())
            ).apply(
                (fst, snd) -> fst.isPresent() && snd.isPresent()
                    ? Optional.of(new Tuple2<>(fst.get(), snd.get()))
                    : Optional.<Tuple2<GithubApi, LinesOfCode>>empty()
            ).flatMap(
                o -> o.apply(this::findLinesOfCodeItem)
            ).flatMap(linesOfCodeItem ->
                this.github.findLastLinesOfCodeStat(repo)
                    .map(stat -> new Tuple2<>(stat, linesOfCodeItem))
            );
    }

    private Optional<Item> findLinesOfCodeItem(
        GithubApi githubStat,
        LinesOfCode linesOfCodeStat
    ) {
        return
            new Unchecked<>(
                new LogNotFound(
                    githubStat,
                    linesOfCodeStat,
                    new ExactItem(githubStat, linesOfCodeStat),
                    LoggerFactory.getLogger(LogNotFound.class)
                )
            ).value();
    }

    private boolean shouldSubmit(Item item) {
        return item.getLinesOfCode() >= 5_000L;
    }

    @AllArgsConstructor(onConstructor_={@Autowired})
    private static class Submitter {

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        // @todo #92 TooSmall: add test case with transaction rollback
        @Transactional
        public Void submit(
            GithubRepoStat stat,
            Item item
        ) {
            final CodexiaReview review = this.review(stat, item);
            this.repository.save(this.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
            return null;
        }

        private TooSmallResult result(GithubRepoStat stat) {
            return new TooSmallResult()
                .setGithubRepo(stat.getGithubRepo())
                .setGithubRepoStat(stat)
                .setState(TooSmallResult.State.RESET);
        }

        private CodexiaReview review(
            GithubRepoStat stat,
            Item item
        ) {
            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo is not small anymore (LoC is %d).",
                        item.getLinesOfCode()
                    )
                )
                .setAuthor(Bot.Type.TOO_SMALL.name())
                .setReason(
                    String.valueOf(
                        item.getLinesOfCode()
                    )
                )
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(stat.getGithubRepo())
                );
        }

        private CodexiaMeta meta(CodexiaReview review) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("too-small")
                .setValue("false");
        }
    }
}

