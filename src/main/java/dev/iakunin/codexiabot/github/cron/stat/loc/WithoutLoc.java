package dev.iakunin.codexiabot.github.cron.stat.loc;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.service.LinesOfCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WithoutLoc implements Runnable {

    private final GithubRepoRepository githubRepoRepository;

    private final LinesOfCode linesOfCode;

    @Transactional
    public void run() {
        try (var repos = this.githubRepoRepository.findAllWithoutLinesOfCode()) {
            new FaultTolerant(
                repos.map(repo -> () -> this.linesOfCode.calculate(repo)),
                tr -> log.error("Unable to calculate LoC", tr.getCause())
            ).run();
        }
    }
}
