package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.entity.TooSmallResult.State;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.LinesOfCode.Item;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("bot.toosmall.NotSmall")
@RequiredArgsConstructor
public final class NotSmall implements Bot {

    private static final long THRESHOLD = 5_000L;

    private final GithubModule github;

    private final TooSmallResultRepository repository;

    private final CodexiaModule codexia;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.github
            .findAllInCodexia()
            .filter(repo ->
                this.codexia
                    .findCodexiaProject(repo)
                    .map(project -> project.level() == 0)
                    .orElse(false)
            )
            .filter(repo -> {
                final var optional = this.repository.findFirstByGithubRepoOrderByIdDesc(repo);
                return optional.isPresent() && optional.get().getState() == State.SET;
            });
    }

    @Override
    public boolean shouldSubmit(final Item item) {
        return item.getLinesOfCode() >= THRESHOLD;
    }

    @Override
    public TooSmallResult result(final GithubRepoStat stat) {
        return new TooSmallResult()
            .setGithubRepo(stat.getGithubRepo())
            .setGithubRepoStat(stat)
            .setState(State.RESET);
    }

    @Override
    public CodexiaReview review(final GithubRepoStat stat, final Item item) {
        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo is not small anymore (LoC is %d).",
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
    public CodexiaMeta meta(final CodexiaReview review) {
        return new CodexiaMeta()
            .setCodexiaProject(review.getCodexiaProject())
            .setKey("too-small")
            .setValue("false");
    }

    @Override
    public void badge(final CodexiaProject project) {
        this.codexia.applyBadge(
            new CodexiaBadge()
                .setCodexiaProject(project)
                .setBadge("bad")
                .setDeletedAt(LocalDateTime.now())
        );
    }
}
