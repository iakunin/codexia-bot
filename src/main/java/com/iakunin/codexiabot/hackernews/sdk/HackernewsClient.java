package com.iakunin.codexiabot.hackernews.sdk;

import com.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.time.Instant;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
    name = "hackernewsClient",
    url = "https://hacker-news.firebaseio.com/v0/",
    configuration = GeneralClientConfiguration.class
)
public interface HackernewsClient {

    @RequestMapping(
        value = "/item/{itemId}.json",
        produces = { "application/json" },
        method = RequestMethod.GET
    )
    ResponseEntity<Item> getItem(@PathVariable("itemId") Integer itemId);

    @Data
    class Item {
        private Integer id;
        private String type;
        private String by;
        private String title;
        private String text;
        private String url;
        private Instant time;
        private boolean deleted = false;
    }
}
