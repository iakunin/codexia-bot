package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class FoundOnReddit {

    private static final Bot.Type BOT_TYPE = Bot.Type.FOUND_ON_REDDIT;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    @Scheduled(cron="${app.cron.bot.found-on-reddit:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubModule.findAllInCodexia()
            .stream()
            .flatMap(
                githubRepo -> {
                    final Set<GithubRepoSource> allRepoSources = this.githubModule.findAllRepoSources(githubRepo);

                    final CodexiaProject codexiaProject = this.codexiaModule
                        .findCodexiaProject(githubRepo)
                        .orElseThrow(
                            () -> new RuntimeException(
                                String.format(
                                    "Unable to find CodexiaProject for githubRepoId='%s'",
                                    githubRepo.getId()
                                )
                            )
                        );

                    return allRepoSources.stream()
                        .filter(githubRepoSource -> githubRepoSource.getSource() == GithubModule.Source.REDDIT)
                        .map(
                            redditSource -> new TmpDto(codexiaProject, redditSource)
                        );
                }
            )
            .filter(
                dto -> !this.codexiaModule.isReviewExist(
                    dto.getCodexiaProject(),
                    BOT_TYPE.name(),
                    dto.getRedditSource().getExternalId()
                )
            )
            .map(
                dto -> new CodexiaReview()
                    .setText(
                        String.format(
                            "This project is found on Reddit: " +
                            "[web link](https://www.reddit.com/comments/%s), " +
                            "[api link](https://www.reddit.com/api/info.json?id=t3_%s)",
                            dto.getRedditSource().getExternalId(),
                            dto.getRedditSource().getExternalId()
                        )
                    )
                    .setAuthor(BOT_TYPE.name())
                    .setReason(dto.getRedditSource().getExternalId())
                    .setCodexiaProject(dto.getCodexiaProject())
            )
            .forEach(
                review -> {
                    this.codexiaModule.sendReview(review);
                    this.codexiaModule.sendMeta(
                        review.getCodexiaProject(),
                        "reddit-id",
                        this.codexiaModule
                            .findAllReviews(review.getCodexiaProject(), review.getAuthor())
                            .stream()
                            .map(CodexiaReview::getReason)
                            .collect(Collectors.joining(","))
                    );
                }
            );

        log.info("Exiting from {}", this.getClass().getName());
    }

    @Value
    // @todo #6 get rid of FoundOnReddit.TmpDto using `org.javatuples.Pair`
    private static final class TmpDto {
        private CodexiaProject codexiaProject;
        private GithubRepoSource redditSource;
    }
}
