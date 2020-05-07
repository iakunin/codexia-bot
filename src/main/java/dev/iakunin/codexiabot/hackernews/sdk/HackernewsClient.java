package dev.iakunin.codexiabot.hackernews.sdk;

import dev.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.time.Instant;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "hackernewsClient",
    url = "${app.hackernews.base-url}",
    configuration = GeneralClientConfiguration.class
)
public interface HackernewsClient {

    @GetMapping(
        value = "/item/{itemId}.json",
        produces = { "application/json" }
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
