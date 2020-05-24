package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.repository.CodexiaProjectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DatabaseTest {

    private static final int EXTERNAL_ID = 333;

    @Test
    public void exists() {
        final var repository = Mockito.mock(CodexiaProjectRepository.class);
        final var project = this.project();
        final var database = new Database(repository);
        Mockito.when(repository.existsByExternalId(DatabaseTest.EXTERNAL_ID)).thenReturn(true);

        database.write(project);

        Mockito.verify(repository, Mockito.times(1)).existsByExternalId(DatabaseTest.EXTERNAL_ID);
    }

    @Test
    public void notExists() {
        final var repository = Mockito.mock(CodexiaProjectRepository.class);
        final var project = this.project();
        final var database = new Database(repository);
        Mockito.when(repository.existsByExternalId(DatabaseTest.EXTERNAL_ID)).thenReturn(false);
        Mockito.when(repository.save(project)).thenReturn(project);

        database.write(project);

        Mockito.verify(repository, Mockito.times(1)).existsByExternalId(DatabaseTest.EXTERNAL_ID);
        Mockito.verify(repository, Mockito.times(1)).save(project);
    }

    private CodexiaProject project() {
        return new CodexiaProject()
            .setExternalId(DatabaseTest.EXTERNAL_ID);
    }
}
