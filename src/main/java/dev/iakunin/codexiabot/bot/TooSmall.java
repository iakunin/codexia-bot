package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.javatuples.Triplet;
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
            .map(
                repo -> new Triplet<>(
                    this.github.findLastLinesOfCodeStat(repo),
                    this.github.findLastGithubApiStat(repo)
                        .map(stat -> (GithubApi) stat.getStat()),
                    this.github.findLastLinesOfCodeStat(repo)
                        .map(stat -> (LinesOfCode) stat.getStat())
                )
            )
            .filter(
                pair ->
                    pair.getValue0().isPresent()
                    && pair.getValue1().isPresent()
                    && pair.getValue2().isPresent()
            )
            .map(
                pair -> new Pair<>(
                    pair.getValue0().get(),
                    this.findDesiredItem(
                        pair.getValue1().get(),
                        pair.getValue2().get()
                    )
                )
            )
            .filter(pair -> pair.getValue1().isPresent())
            .map(
                pair -> new Pair<>(
                    pair.getValue0(),
                    pair.getValue1().get()
                )
            )
            .filter(pair -> this.shouldSubmit(pair.getValue1()))
            .forEach(this.submitter::submit);
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
        public void submit(Pair<GithubRepoStat, LinesOfCode.Item> pair) {
            final CodexiaReview review = this.review(pair);
            this.repository.save(this.result(pair.getValue0()));
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
        }

        private TooSmallResult result(GithubRepoStat stat) {
            return new TooSmallResult()
                .setGithubRepo(stat.getGithubRepo())
                .setGithubRepoStat(stat)
                .setState(TooSmallResult.State.SET);
        }

        private CodexiaReview review(Pair<GithubRepoStat, LinesOfCode.Item> pair) {
            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo is too small (LoC is %d).",
                        pair.getValue1().getLinesOfCode()
                    )
                )
                .setAuthor(Bot.Type.TOO_SMALL.name())
                .setReason(
                    String.valueOf(
                        pair.getValue1().getLinesOfCode()
                    )
                )
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(pair.getValue0().getGithubRepo())
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

