package dev.iakunin.codexiabot.codexia.controller;

import dev.iakunin.codexiabot.codexia.sdk.CodexiaClient;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
@Slf4j
public final class TestController {

    private CodexiaClient codexiaClient;

    @GetMapping("/api/v1/codexia/getRepo/{page}")
    @ResponseBody
    public ResponseEntity<?> createRepo(
        @PathVariable Integer page
    ) {

        final List<CodexiaClient.Project> body = this.codexiaClient.getItem(page).getBody();
        Objects.requireNonNull(body);

        return new ResponseEntity<>(
            body,
            HttpStatus.OK
        );
    }
}
