package dev.iakunin.codexiabot.codexia.sdk;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.iakunin.codexiabot.codexia.config.FeignConfig;
import dev.iakunin.codexiabot.common.config.feign.GeneralClientConfiguration;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.cactoos.list.ListOf;
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

    @GetMapping(value = "/recent.json", produces = "application/json")
    ResponseEntity<List<Project>> getRecent(
        @RequestParam("page") Integer page
    );

    @GetMapping(value = "/p/{projectId}.json", produces = "application/json")
    ResponseEntity<Project> getProject(
        @PathVariable("projectId") Integer projectId
    );

    @PostMapping(value = "/p/{projectId}/post", produces = "application/json")
    ResponseEntity<String> createReview(
        @PathVariable("projectId") Integer projectId,
        @RequestParam("text") String text,
        @RequestParam("hash") String hash
    );

    @PostMapping(value = "/p/{projectId}/meta", produces = "application/json")
    ResponseEntity<String> setMeta(
        @PathVariable("projectId") Integer projectId,
        @RequestParam("key") String key,
        @RequestParam("value") String value
    );

    @PostMapping(value = "/p/{projectId}/attach", produces = "application/json")
    ResponseEntity<String> attachBadge(
        @PathVariable("projectId") Integer projectId,
        @RequestParam("text") String badge
    );

    @PostMapping(value = "/p/{projectId}/detach/{badge}", produces = "application/json")
    ResponseEntity<String> detachBadge(
        @PathVariable("projectId") Integer projectId,
        @PathVariable("badge") String badge
    );

    @Data
    class Project {
        private Integer id;
        private String coordinates;
        private Submitter submitter;
        private String deleted;

        // @todo #6 write tests for different timezones
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z")
        // @todo #6 replace `Date` with `ZonedDateTime`
        private Date created;

        private List<Badge> badges = new ListOf<>();

        @Data
        public static class Submitter {
            private Integer id;
            private String login;
        }

        @Data
        public static class Badge {
            private Integer id;
            private String text;
        }
    }

    enum ReviewStatus {
        ALREADY_EXISTS(404);

        private final int httpStatus;

        ReviewStatus(int httpStatus) {
            this.httpStatus = httpStatus;
        }

        public int httpStatus() {
            return this.httpStatus;
        }
    }
}
