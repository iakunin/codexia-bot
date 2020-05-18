package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.service.Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class MissingFiller implements Runnable {

    private final CodexiaProjectRepository repository;

    private final Writer writer;

    @Autowired
    public MissingFiller(
        CodexiaProjectRepository repository,
        @Qualifier("codexia.service.GithubRepo") Writer writer
    ) {
        this.repository = repository;
        this.writer = writer;
    }

    @Transactional
    public void run() {
        try (var projects = this.repository.findAllWithoutGithubRepo()) {
            projects.forEach(this.writer::write);
        }
    }
}
