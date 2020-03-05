package com.iakunin.codexiabot.codexia.sdk.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "codexiaClient",
    url = "https://www.codexia.org/",
    configuration = GeneralClientConfiguration.class
)
public interface Codexia {

    @RequestMapping(
        value = "/recent.json",
        produces = { "application/json" },
        method = RequestMethod.GET
    )
    ResponseEntity<List<Project>> getItem(
        @RequestParam("page") Integer page,
        @RequestHeader("X-Codexia-Token") String codexiaToken
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
