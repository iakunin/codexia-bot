package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GithubRepoUnitTest {

    @Test
    public void emptyUrl() {
        new GithubRepo(Mockito.mock(GithubModule.class))
            .write(new HackernewsItem());
    }

    @Test
    public void gist() {
        new GithubRepo(Mockito.mock(GithubModule.class))
            .write(new HackernewsItem().setUrl("gist.github.com"));
    }

    @Test
    public void runtimeExceptionDuringRepoCreate() throws IOException {
        final GithubModule mock = Mockito.mock(GithubModule.class);
        Mockito.doThrow(new RuntimeException())
            .when(mock)
            .createRepo(Mockito.any());

        new GithubRepo(mock)
            .write(new HackernewsItem().setUrl("github.com/first"));
    }

    @Test
    public void ioExceptionDuringRepoCreate() throws IOException {
        final GithubModule mock = Mockito.mock(GithubModule.class);
        Mockito.doThrow(new IOException())
            .when(mock)
            .createRepo(Mockito.any());

        new GithubRepo(mock)
            .write(new HackernewsItem().setUrl("github.com/second"));
    }
}
