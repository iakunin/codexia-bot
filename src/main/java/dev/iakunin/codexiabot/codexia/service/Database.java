package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("codexia.service.Database")
@Slf4j
@RequiredArgsConstructor
public class Database implements Writer {

    private final CodexiaProjectRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(CodexiaProject project) {
        log.debug("Got CodexiaProject: {}", project);

        if (!this.repository.existsByExternalId(project.getExternalId())) {
            log.debug("Saving new CodexiaProject: {}", project);
            this.repository.save(project);
        }
    }
}
