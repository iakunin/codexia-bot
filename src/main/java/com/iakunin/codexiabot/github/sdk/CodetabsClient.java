package com.iakunin.codexiabot.github.sdk;

import com.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "codetabsClient",
    url = "https://api.codetabs.com/v1/",
    configuration = GeneralClientConfiguration.class
)
public interface CodetabsClient {

    @RequestMapping(
        value = "/loc",
        produces = { "application/json" },
        method = RequestMethod.GET
    )
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
