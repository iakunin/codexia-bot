package dev.iakunin.codexiabot.github.cron.stat.loc;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.service.LinesOfCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Codexia implements Runnable {

    private final GithubModule github;

    private final LinesOfCode calculator;

    @Transactional
    public void run() {
        try (var repos = this.github.findAllInCodexia()) {
            new FaultTolerant(
                repos.map(repo -> () -> this.calculator.calculate(repo)),
                tr -> log.error("Unable to calculate LoC", tr.getCause())
            ).run();
        }
    }
}
