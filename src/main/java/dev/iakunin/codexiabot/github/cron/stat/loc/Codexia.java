package dev.iakunin.codexiabot.github.cron.stat.loc;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.service.LinesOfCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor
public class Codexia implements Runnable {

    private final GithubModule github;

    private final LinesOfCode linesOfCode;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexia()) {
            repos.forEach(this.linesOfCode::calculate);
        }
    }
}
