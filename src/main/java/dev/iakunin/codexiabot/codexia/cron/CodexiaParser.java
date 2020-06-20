package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.codexia.service.Writer;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class CodexiaParser implements Runnable {

    private final CodexiaClient codexia;

    private final CodexiaProjectRepository repository;

    private final Writer writer;

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void run() {
        int page = 0;
        List<CodexiaClient.Project> projects;

        do {
            projects = this.codexia.getRecent(page).getBody();
            Objects.requireNonNull(projects);

            new FaultTolerant(
                projects.stream()
                    .filter(project -> project.getDeleted() == null)
                    .filter(
                        project -> !this.repository.existsByExternalId(project.getId())
                    )
                    .map(CodexiaProject.Factory::from)
                    .map(project -> () -> this.writer.write(project)),
                tr -> log.error("Unable to write project", tr.getCause())
            ).run();

            page += 1;
        } while (!projects.isEmpty());
    }
}
