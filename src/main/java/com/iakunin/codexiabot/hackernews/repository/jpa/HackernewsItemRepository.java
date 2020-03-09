package com.iakunin.codexiabot.hackernews.repository.jpa;

import com.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HackernewsItemRepository extends JpaRepository<HackernewsItem, Long> {

    Boolean existsByExternalId(Integer externalId);

    Optional<HackernewsItem> findByExternalId(Integer externalId);

    @Query("select max(externalId) from HackernewsItem")
    Integer getMaxExternalId();

    List<HackernewsItem> findAllByType(String type);
}
