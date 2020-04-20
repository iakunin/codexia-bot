package dev.iakunin.codexiabot.bot.repository;

import dev.iakunin.codexiabot.bot.entity.TooManyStarsResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TooManyStarsResultRepository extends JpaRepository<TooManyStarsResult, Long>, ResultRepository {
    /*_*/
}
