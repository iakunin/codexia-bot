package com.iakunin.codexiabot.hackernews.repository;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HackernewsItemRepository extends JpaRepository<HackernewsItem, Long> {

    @Query(
        value = "select i.external_id from item i " +
        "where i.external_id in (?1)",
        nativeQuery = true
    )
    List<String> findExternalIdList(List<String> externalIdList);

    Optional<HackernewsItem> findByExternalId(String externalId);
}
