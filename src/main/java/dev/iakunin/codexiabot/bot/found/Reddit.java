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
public final class Reddit implements Bot {

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.githubModule
            .findAllInCodexia();
    }

    @Override
    public GithubModule.Source source() {
        return GithubModule.Source.REDDIT;
    }

    @Override
    public String reviewText(String externalId) {
        return
            new FormattedText(
                new Joined(
                    " ",
                    "This project is found on Reddit:",
                    "[web link](https://www.reddit.com/comments/%s),",
                    "[api link](https://www.reddit.com/api/info.json?id=t3_%s)."
                ),
                externalId,
                externalId
            ).toString();
    }

    @Override
    public CodexiaMeta meta(CodexiaReview review) {
        try (var reviews = this.codexiaModule
            .findAllReviews(review.getCodexiaProject(), review.getAuthor())
        ) {
            return new CodexiaMeta()
                .setCodexiaProject(review.getCodexiaProject())
                .setKey("reddit-id")
                .setValue(
                    reviews
                        .map(CodexiaReview::getReason)
                        .collect(Collectors.joining(","))
                );
        }
    }
}
