package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class TooSmall implements Runnable {

    private final GithubModule github;

    private final Submitter submitter;

    public TooSmall(
        GithubModule github,
        CodexiaModule codexia
    ) {
        this.github = github;
        this.submitter = new Submitter(codexia);
    }

    public void run() {
        this.github.findAllInCodexia()
            .stream()
            .filter(
                // @todo #92 filter if review already exist
                repo -> Optional.empty().isEmpty()
            )
            // @todo #92 add github::findLastLinesOfCodeStat
            .map(this.github::findLastGithubApiStat)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(this::shouldSubmit)
            .forEach(this.submitter::submit);
    }

    private boolean shouldSubmit(GithubRepoStat stat) {
        final LinesOfCode lines = (LinesOfCode) stat.getStat();

        // @todo #92 implement algorithm of finding current LoC only for repo main language
        return false;
    }


    @Slf4j
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
                .setAuthor(Bot.Type.TOO_MANY_STARS.name())
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

