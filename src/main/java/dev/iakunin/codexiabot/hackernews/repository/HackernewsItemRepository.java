package dev.iakunin.codexiabot.hackernews.repository;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HackernewsItemRepository extends JpaRepository<HackernewsItem, Long> {

    boolean existsByExternalId(Integer externalId);

    Optional<HackernewsItem> findByExternalId(Integer externalId);

    @Query("select coalesce(max(externalId), 0) from HackernewsItem")
    Integer getMaxExternalId();

    Stream<HackernewsItem> findAllByType(String type);
}
