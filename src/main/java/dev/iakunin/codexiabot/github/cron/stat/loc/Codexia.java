package dev.iakunin.codexiabot.github.cron.stat.loc;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.service.LinesOfCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public final class Codexia implements Runnable {

    private final GithubModule github;

    private final LinesOfCode linesOfCode;

    public void run() {
        this.github
            .findAllInCodexia()
            .forEach(
                this.linesOfCode::calculate
            );
    }
}
