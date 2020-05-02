package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("codexia.service.GithubRepo")
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class GithubRepo implements Writer {

    private final GithubModule githubModule;

    @Override
    public void write(CodexiaProject project) {
        final String url = "https://github.com/" + project.getCoordinates();
        try {
            this.githubModule.createRepo(
                new GithubModule.CreateArguments()
                    .setUrl(url)
                    .setSource(GithubModule.Source.CODEXIA)
                    .setExternalId(String.valueOf(project.getExternalId()))
            );
        } catch (RuntimeException | IOException e) {
            log.error("Unable to create github repo; source url='{}'", url, e);
        }
    }
}
