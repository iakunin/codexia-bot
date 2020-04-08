package dev.iakunin.codexiabot.hackernews.repository;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.util.List;
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

    List<HackernewsItem> findAllByType(String type);

    @Query(
        value = "select gen.external_id from hackernews_item h " +
            " right join ( " +
            "    select generate_series as external_id " +
            "    from generate_series(?1, ?2) " +
            ") gen " +
            "on gen.external_id = h.external_id " +
            "where h.external_id is null",
        nativeQuery = true
    )
    Stream<Integer> findAbsentExternalIds(Integer from, Integer to);
}
