package dev.iakunin.codexiabot.github.sdk;

import dev.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "codetabsClient",
    url = "${app.codetabs.base-url}",
    configuration = GeneralClientConfiguration.class
)
public interface CodetabsClient {

    @GetMapping(value = "/loc", produces = "application/json")
    ResponseEntity<List<Item>> getLinesOfCode(@RequestParam("github") String repoName);

    @Data
    final class Item {
        private String language;
        private Integer files;
        private Integer lines;
        private Integer blanks;
        private Integer comments;
        private Integer linesOfCode;
    }
}
