package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.cron.updateprojects.Updater;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public final class UpdateProjects implements Runnable {

    private static final int PAGE_SIZE = 100;

    private final CodexiaProjectRepository repository;

    private final CodexiaClient codexia;

    private final Updater updater;

    public void run() {
        Page<CodexiaProject> page;
        int num = 0;
        do {
            page = this.repository.findAllActive(PageRequest.of(num, PAGE_SIZE));
            new FaultTolerant(
                page.stream()
                    .map(project -> () ->
                        this.updater.update(
                            Objects.requireNonNull(
                                this.codexia
                                    .getProject(project.getExternalId())
                                    .getBody()
                            )
                        )
                    ),
                tr -> log.error("Unable to update project", tr.getCause())
            ).run();
            num += 1;
        } while (page.hasNext());
    }
}
