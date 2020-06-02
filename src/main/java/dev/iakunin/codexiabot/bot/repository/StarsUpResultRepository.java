package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarsUpResultRepository
    extends JpaRepository<StarsUpResult, Long>, ResultRepository {
    /*_*/
}
