package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("codexia.service.Database")
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Database implements Writer {

    private final CodexiaProjectRepository repository;

    @Override
    public void write(CodexiaProject project) {
        log.debug("Got CodexiaProject: {}", project);

        if (!this.repository.existsByExternalId(project.getExternalId())) {
            log.debug("Saving new CodexiaProject: {}", project);
            this.repository.save(project);
        }
    }
}
