package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bot.toosmall.TooSmall")
@AllArgsConstructor(onConstructor_={@Autowired})
public final class TooSmall implements Bot {

    private final GithubModule github;

    private final TooSmallResultRepository repository;

    private final CodexiaModule codexia;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.github
            .findAllInCodexia()
            .stream()
            .filter(repo -> {
                final Optional<TooSmallResult> optional = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);
                return optional.isEmpty() || optional.get().getState() == TooSmallResult.State.RESET;
            });
    }

    @Override
    public boolean shouldSubmit(GithubRepoStat.LinesOfCode.Item item) {
        return item.getLinesOfCode() < 5_000L;
    }

    @Override
    public TooSmallResult result(GithubRepoStat stat) {
        return new TooSmallResult()
            .setGithubRepo(stat.getGithubRepo())
            .setGithubRepoStat(stat)
            .setState(TooSmallResult.State.SET);
    }

    @Override
    public CodexiaReview review(GithubRepoStat stat, GithubRepoStat.LinesOfCode.Item item) {
        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo is too small (LoC is %d).",
                    item.getLinesOfCode()
                )
            )
            .setAuthor(dev.iakunin.codexiabot.bot.Bot.Type.TOO_SMALL.name())
            .setReason(
                String.valueOf(
                    item.getLinesOfCode()
                )
            )
            .setCodexiaProject(
                this.codexia.getCodexiaProject(stat.getGithubRepo())
            );
    }

    @Override
    public CodexiaMeta meta(CodexiaReview review) {
        return new CodexiaMeta()
            .setCodexiaProject(review.getCodexiaProject())
            .setKey("too-small")
            .setValue("true");
    }
}
