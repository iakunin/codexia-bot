package dev.iakunin.codexiabot.bot.found;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Hackernews implements Bot {

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.githubModule
            .findAllInCodexiaAndHackernews()
            .stream();
    }

    @Override
    public GithubModule.Source source() {
        return GithubModule.Source.HACKERNEWS;
    }

    @Override
    public String reviewText(String externalId) {
        return
            new FormattedText(
                new Joined(
                    " ",
                    "This project is found on Hackernews:",
                    "[web link](https://news.ycombinator.com/item?id=%s),",
                    "[api link](https://hacker-news.firebaseio.com/v0/item/%s.json)."
                ),
                externalId,
                externalId
            ).toString();
    }

    @Override
    public CodexiaMeta meta(CodexiaReview review) {
        return new CodexiaMeta()
            .setCodexiaProject(review.getCodexiaProject())
            .setKey("hacker-news-id")
            .setValue(
                this.codexiaModule
                    .findAllReviews(review.getCodexiaProject(), review.getAuthor())
                    .stream()
                    .map(CodexiaReview::getReason)
                    .collect(Collectors.joining(","))
            );
    }
}
