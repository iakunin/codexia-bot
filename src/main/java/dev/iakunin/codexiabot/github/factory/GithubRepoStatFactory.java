package dev.iakunin.codexiabot.github.factory;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.sdk.CodetabsClient;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.kohsuke.github.GHRepository;

public final class GithubRepoStatFactory {
    private GithubRepoStatFactory() { }

    public static GithubRepoStat from(final GHRepository from) {
        return new GithubRepoStat()
            .setStat(
                new GithubRepoStat.GithubApi()
                    .setDescription(from.getDescription())
                    .setPushedAt(GithubRepoStatFactory.toLocalDateTime(from.getPushedAt()))
                    .setLanguage(from.getLanguage())
                    .setHomepage(from.getHomepage())
                    .setForks(from.getForksCount())
                    .setWatchers(from.getWatchersCount())
                    .setStars(from.getStargazersCount())
            );
    }

    public static GithubRepoStat from(final List<CodetabsClient.Item> from) {
        return new GithubRepoStat()
            .setStat(
                new GithubRepoStat.LinesOfCode()
                    .setItemList(
                        from.stream().map(item -> new GithubRepoStat.LinesOfCode.Item()
                            .setLanguage(item.getLanguage())
                            .setFiles(item.getFiles())
                            .setLines(item.getLines())
                            .setBlanks(item.getBlanks())
                            .setComments(item.getComments())
                            .setLinesOfCode(item.getLinesOfCode())
                        ).collect(Collectors.toList())
                    )
            );
    }

    private static LocalDateTime toLocalDateTime(final Date date) {
        return date.toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }
}
