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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(
        value = "/recent.json",
        produces = { "application/json" },
        method = RequestMethod.GET
    )
    ResponseEntity<List<Project>> getItem(
        @RequestParam("page") Integer page
    );

    @RequestMapping(
        value = "/p/{projectId}/post",
        produces = { "application/json" },
        method = RequestMethod.POST
    )
    ResponseEntity<String> createReview(
        @PathVariable("projectId") String projectId,
        @RequestParam("text") String text
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
