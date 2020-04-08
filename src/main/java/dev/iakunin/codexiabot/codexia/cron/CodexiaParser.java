package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.codexia.service.Writer;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public final class CodexiaParser {

    private final CodexiaClient codexiaClient;
    private final CodexiaProjectRepository repository;
    private final Writer writer;

    @Scheduled(cron="${app.cron.codexia.codexia-parser:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        int page = 0;
        List<CodexiaClient.Project> projectList;

        do {
            projectList = this.codexiaClient.getRecent(page).getBody();
            Objects.requireNonNull(projectList);

            projectList.stream()
                .filter(project -> project.getDeleted() == null)
                .filter(
                    project -> !this.repository.existsByExternalId(project.getId())
                )
                .map(CodexiaProject.Factory::from)
                .forEach(this.writer::write)
            ;

            page++;
        } while (!projectList.isEmpty());

        log.info("Exiting from {}", this.getClass().getName());
    }
}
