package dev.iakunin.codexiabot.github.cron.stat.loc;

import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.service.LinesOfCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public final class WithoutLoc implements Runnable {

    private final GithubRepoRepository githubRepoRepository;

    private final LinesOfCode linesOfCode;

    public void run() {
        this.githubRepoRepository
            .findAllWithoutLinesOfCode()
            .forEach(
                this.linesOfCode::calculate
            );
    }
}
