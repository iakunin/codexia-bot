package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import io.vavr.Tuple2;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public final class TooSmall implements Runnable {

    private final GithubModule github;

    private final Submitter submitter;

    private final TooSmallResultRepository repository;

    public TooSmall(
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
                final var optional = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);
                return optional.isEmpty() || optional.get().getState() == TooSmallResult.State.RESET;
            })
            .map(this::prepare)
            .forEach(
                optional -> optional
                    .filter(pair -> this.shouldSubmit(pair._2()))
                    .ifPresent(pair -> this.submitter.submit(pair._1(), pair._2()))
            )
        ;
    }

    private Optional<Tuple2<GithubRepoStat, LinesOfCode.Item>> prepare(GithubRepo repo) {
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
                o -> o.apply(this::findDesiredItem)
            ).flatMap(linesOfCodeItem ->
                this.github.findLastLinesOfCodeStat(repo)
                    .map(stat -> new Tuple2<>(stat, linesOfCodeItem))
            );
    }

    // @todo #92 Extract it to a separate class & write a unit-test for it
    private Optional<LinesOfCode.Item> findDesiredItem(
        GithubApi githubStat,
        LinesOfCode linesOfCodeStat
    ) {
        final Optional<LinesOfCode.Item> firstAttempt = linesOfCodeStat
            .getItemList()
            .stream()
            .filter(
                item -> item.getLanguage().toLowerCase().equals(
                    githubStat.getLanguage().toLowerCase()
                )
            )
            .findFirst();

        final Optional<LinesOfCode.Item> secondAttempt = linesOfCodeStat
            .getItemList()
            .stream()
            .filter(
                item ->
                    item.getLanguage().toLowerCase().contains(githubStat.getLanguage().toLowerCase())
                        ||
                    githubStat.getLanguage().toLowerCase().contains(item.getLanguage().toLowerCase())
            )
            .findFirst();

        final Optional<LinesOfCode.Item> result = firstAttempt.or(() -> secondAttempt);

        result.ifPresentOrElse(
            item -> {},
            () -> log.warn(
                "Unable to find proper LoC stat; language='{}'; LoC list='{}'",
                githubStat.getLanguage(),
                linesOfCodeStat.getItemList()
            )
        );

        return result;
    }

    private boolean shouldSubmit(LinesOfCode.Item item) {
        return item.getLinesOfCode() < 5_000L;
    }

    @AllArgsConstructor(onConstructor_={@Autowired})
    private static class Submitter {

        private final CodexiaModule codexia;

        private final TooSmallResultRepository repository;

        // @todo #92 TooSmall: add test case with transaction rollback
        @Transactional
        public void submit(
            GithubRepoStat stat,
            LinesOfCode.Item item
        ) {
            final CodexiaReview review = this.review(stat, item);
            this.repository.save(this.result(stat));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
        }

        private TooSmallResult result(GithubRepoStat stat) {
            return new TooSmallResult()
                .setGithubRepo(stat.getGithubRepo())
                .setGithubRepoStat(stat)
                .setState(TooSmallResult.State.SET);
        }

        private CodexiaReview review(
            GithubRepoStat stat,
            LinesOfCode.Item item
        ) {
            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo is too small (LoC is %d).",
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
                .setValue("true");
        }
    }
}

