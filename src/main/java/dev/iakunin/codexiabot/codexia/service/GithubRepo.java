package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("codexia.service.GithubRepo")
@Slf4j
@RequiredArgsConstructor
public class GithubRepo implements Writer {

    private final GithubModule githubModule;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(CodexiaProject project) {
        final String url = "https://github.com/" + project.getCoordinates();
        try {
            this.githubModule.createRepo(
                new GithubModule.CreateArguments(
                    url,
                    GithubModule.Source.CODEXIA,
                    String.valueOf(project.getExternalId())
                )
            );
        } catch (RuntimeException | IOException e) {
            log.error("Unable to create github repo; source url='{}'", url, e);
        }
    }
}
