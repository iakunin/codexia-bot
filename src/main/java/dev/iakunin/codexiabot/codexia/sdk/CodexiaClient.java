package dev.iakunin.codexiabot.codexia.sdk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.iakunin.codexiabot.codexia.config.FeignConfig;
import dev.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "codexiaClient",
    url = "${app.codexia.base-url}",
    configuration = {
        GeneralClientConfiguration.class,
        FeignConfig.class
    }
)
public interface CodexiaClient {

    @GetMapping(
        value = "/recent.json",
        produces = { "application/json" }
    )
    ResponseEntity<List<Project>> getRecent(
        @RequestParam("page") Integer page
    );

    @GetMapping(
        value = "/p/{projectId}.json",
        produces = { "application/json" }
    )
    ResponseEntity<Project> getProject(
        @PathVariable("projectId") Integer projectId
    );

    @PostMapping(
        value = "/p/{projectId}/post",
        produces = { "application/json" }
    )
    ResponseEntity<String> createReview(
        @PathVariable("projectId") Integer projectId,
        @RequestParam("text") String text,
        @RequestParam("hash") String hash
    );

    @PostMapping(
        value = "/p/{projectId}/meta",
        produces = { "application/json" }
    )
    ResponseEntity<String> setMeta(
        @PathVariable("projectId") Integer projectId,
        @RequestParam("key") String key,
        @RequestParam("value") String value
    );

    @Data
    class Project {
        private Integer id;
        private String coordinates;
        private String author;

        @JsonProperty("author_id")
        private Integer authorId;
        private String deleted;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z")
        private Date created;
    }
}
