package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public final class TooSmall implements Runnable {

    private final GithubModule github;

    private final Submitter submitter;

    private final TooSmallResultRepository resultRepository;

    public TooSmall(
        GithubModule github,
        CodexiaModule codexia,
        TooSmallResultRepository resultRepository
    ) {
        this.github = github;
        this.resultRepository = resultRepository;
        this.submitter = new Submitter(codexia);
    }

    public void run() {
        this.github.findAllInCodexia()
            .stream()
            .filter(repo -> {
                final var optional = this.resultRepository.findFirstByGithubRepoOrderByIdDesc(repo);
                return optional.isEmpty() || optional.get().getState() == TooSmallResult.State.RESET;
            })
            .map(
                repo -> new Pair<>(
                    this.github.findLastGithubApiStat(repo),
                    this.github.findLastLinesOfCodeStat(repo)
                )
            )
            .filter(
                pair -> pair.getValue0().isPresent() && pair.getValue1().isPresent()
            )
            .map(
                pair -> new Pair<>(
                    pair.getValue0().get().getGithubRepo(),
                    this.findDesiredItem(
                        (GithubRepoStat.GithubApi) pair.getValue0().get().getStat(),
                        (GithubRepoStat.LinesOfCode) pair.getValue1().get().getStat()
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

    private Optional<LinesOfCode.Item> findDesiredItem(
        GithubRepoStat.GithubApi githubStat,
        GithubRepoStat.LinesOfCode linesOfCodeStat
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

        // @todo #92 TooSmall: add test case with transaction rollback
        @Transactional
        public void submit(Pair<GithubRepo, LinesOfCode.Item> pair) {
            final CodexiaReview review = this.review(pair);
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
        }

        private CodexiaReview review(Pair<GithubRepo, LinesOfCode.Item> pair) {
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
                    this.codexia.getCodexiaProject(pair.getValue0())
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

