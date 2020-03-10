package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepoSource;
import dev.iakunin.codexiabot.hackernews.HackernewsModule;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class FoundOnHackernews {

    private static final Bot.Type BOT_TYPE = Bot.Type.FOUND_ON_HACKERNEWS;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final HackernewsModule hackernewsModule;

    @Scheduled(cron="5 * * * * *") // every minute at 5th second
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.hackernewsModule.healthCheckItems(
            this.githubModule.findAllInCodexiaAndHackernews()
                .stream()
                .flatMap(
                    repo -> this.githubModule.findAllRepoSources(repo).stream()
                )
                .filter(
                    source -> source.getSource() == GithubModule.Source.HACKERNEWS
                )
                .map(GithubRepoSource::getExternalId)
                .map(Integer::valueOf)
        );

        this.githubModule.findAllInCodexiaAndHackernews()
            .stream()
            .flatMap(
                githubRepo -> {
                    final Set<GithubRepoSource> allRepoSources = this.githubModule.findAllRepoSources(githubRepo);

                    final CodexiaProject codexiaProject = allRepoSources.stream()
                        .filter(source -> source.getSource() == GithubModule.Source.CODEXIA)
                        .findFirst()
                        .flatMap(
                            source -> this.codexiaModule.findByExternalId(Integer.valueOf(source.getExternalId()))
                        )
                        .orElseThrow(
                            () -> new RuntimeException(
                                String.format(
                                    "Unable to find source with type'%s' for githubRepoId='%s' " +
                                    "or codexiaProject by externalId",
                                    GithubModule.Source.CODEXIA.name(),
                                    githubRepo.getId()
                                )
                            )
                        );

                    return allRepoSources.stream()
                        .filter(githubRepoSource -> githubRepoSource.getSource() == GithubModule.Source.HACKERNEWS)
                        .map(
                            hackerNewsSource -> new TmpDto(codexiaProject, hackerNewsSource)
                        );
                }
            )
            .filter(
                dto -> !this.codexiaModule.isReviewExist(
                    dto.getCodexiaProject(),
                    BOT_TYPE.name(),
                    dto.getHackernewsSource().getExternalId()
                )
            )
            .forEach(
                dto -> this.codexiaModule.sendReview(
                    new CodexiaReview()
                        .setText(
                            String.format(
                                "This project is found on Hackernews: " +
                                "[web link](https://news.ycombinator.com/item?id=%s), " +
                                "[api link](https://hacker-news.firebaseio.com/v0/item/%s.json), ",
                                dto.getHackernewsSource().getExternalId(),
                                dto.getHackernewsSource().getExternalId()
                            )
                        )
                        .setAuthor(BOT_TYPE.name())
                        .setReason(dto.getHackernewsSource().getExternalId())
                        .setCodexiaProject(dto.getCodexiaProject())
                )
            );

        log.info("Exiting from {}", this.getClass().getName());
    }

    @Value
    private static final class TmpDto {
        private CodexiaProject codexiaProject;
        private GithubRepoSource hackernewsSource;
    }
}
