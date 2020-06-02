package dev.iakunin.codexiabot.bot.found;

import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaMeta;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class Reddit implements Bot {

    private final GithubModule github;

    private final CodexiaModule codexia;

    @Override
    public Stream<GithubRepo> repoStream() {
        return this.github.findAllInCodexia();
    }

    @Override
    public GithubModule.Source source() {
        return GithubModule.Source.REDDIT;
    }

    @Override
    public String reviewText(final String id) {
        return
            new FormattedText(
                new Joined(
                    " ",
                    "This project is found on Reddit:",
                    "[web link](https://www.reddit.com/comments/%s),",
                    "[api link](https://www.reddit.com/api/info.json?id=t3_%s)."
                ),
                id,
                id
            ).toString();
    }

    @Override
    public CodexiaMeta meta(final CodexiaReview review) {
        try (var reviews = this.codexia
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
