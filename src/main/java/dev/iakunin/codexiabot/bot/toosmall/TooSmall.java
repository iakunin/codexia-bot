package dev.iakunin.codexiabot.bot.toosmall;

import dev.iakunin.codexiabot.bot.entity.TooSmallResult;
import dev.iakunin.codexiabot.bot.repository.TooSmallResultRepository;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
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
}
