package com.iakunin.codexiabot.codexia.repository;

import com.iakunin.codexiabot.codexia.entity.CodexiaProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodexiaProjectRepository extends JpaRepository<CodexiaProject, Long> {
    Boolean existsByExternalId(Integer externalId);
}
