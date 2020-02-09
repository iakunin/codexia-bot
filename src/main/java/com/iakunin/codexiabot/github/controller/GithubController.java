package com.iakunin.codexiabot.github.controller;

import com.iakunin.codexiabot.github.GithubModule;
import java.io.IOException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
@Slf4j
public final class GithubController {

    private GithubModule githubModule;

    @PostMapping("/api/v1/github/createRepo")
    @ResponseBody
    public ResponseEntity<String> createRepo(
        @NotNull @Valid @RequestBody CreateRepoRequest body
    ) throws IOException {

        this.githubModule.createRepo(body.getUrl());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Data
    private static class CreateRepoRequest {
        private String url;
    }
}
