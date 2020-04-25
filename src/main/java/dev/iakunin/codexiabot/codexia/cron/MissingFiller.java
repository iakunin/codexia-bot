package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.service.Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class MissingFiller implements Runnable {

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

    public void run() {
        this.repository
            .findAllWithoutGithubRepo()
            .forEach(this.writer::write)
        ;
    }
}
