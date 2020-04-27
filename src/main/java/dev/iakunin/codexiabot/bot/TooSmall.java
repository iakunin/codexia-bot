package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class TooSmall implements Runnable {

    private final GithubModule github;

    private final CodexiaModule codexia;

    private final Submitter submitter;

    public TooSmall(
        GithubModule github,
        CodexiaModule codexia
    ) {
        this.github = github;
        this.codexia = codexia;
        this.submitter = new Submitter(codexia);
    }

    public void run() {
        this.github.findAllInCodexia()
            .stream()
            .filter(
                repo -> this.codexia.findAllReviews(
                    this.codexia.getCodexiaProject(repo),
                    Bot.Type.TOO_SMALL.name()
                ).isEmpty()
            )
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
                pair -> new Triplet<>(
                    pair.getValue0().get().getGithubRepo(),
                    (GithubRepoStat.GithubApi) pair.getValue0().get().getStat(),
                    (GithubRepoStat.LinesOfCode) pair.getValue1().get().getStat()
                )
            )
            .filter(this::shouldSubmit)
            .forEach(this.submitter::submit);
    }

    private LinesOfCode.Item findDesiredItem(GithubRepoStat.GithubApi githubStat, GithubRepoStat.LinesOfCode linesOfCodeStat) {
        // @todo #92 implement algorithm of finding current LoC only for repo main language
        return null;
    }

    private boolean shouldSubmit(Pair<GithubRepoStat, GithubRepoStat> pair) {
        final LinesOfCode lines = (LinesOfCode) stat.getStat();

        return false;
    }

    @AllArgsConstructor(onConstructor_={@Autowired})
    private static class Submitter {

        private final CodexiaModule codexia;

        // @todo #92 TooSmall: add test case with transaction rollback
        @Transactional
        public void submit(GithubRepoStat stat) {
            final CodexiaReview review = this.review(stat);
            this.codexia.saveReview(review);
            this.codexia.sendMeta(this.meta(review));
        }

        private CodexiaReview review(GithubRepoStat stat) {
            // @todo #92 insert real LoC
            final int lines = 123;
            return new CodexiaReview()
                .setText(
                    String.format(
                        "The repo is too small (LoC is %d).",
                        lines
                    )
                )
                .setAuthor(Bot.Type.TOO_SMALL.name())
                .setReason(String.valueOf(lines))
                .setCodexiaProject(
                    this.codexia.getCodexiaProject(stat.getGithubRepo())
                );
        }

        private CodexiaMeta meta(CodexiaReview review) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("too-small")
                .setValue(review.getReason());
        }
    }
}

