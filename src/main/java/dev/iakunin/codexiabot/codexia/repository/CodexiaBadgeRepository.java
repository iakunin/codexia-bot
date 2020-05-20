package dev.iakunin.codexiabot.codexia.repository;

import dev.iakunin.codexiabot.codexia.entity.CodexiaBadge;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodexiaBadgeRepository extends JpaRepository<CodexiaBadge, Long> {

    Optional<CodexiaBadge> findByCodexiaProjectAndBadge(CodexiaProject codexiaProject, String badge);

    @Query("select cb from CodexiaBadge cb")
    Stream<CodexiaBadge> getAll();
}
