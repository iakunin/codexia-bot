package com.iakunin.codexiabot.github.controller;

import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<String> test() throws IOException {

        GitHub github = new GitHubBuilder().withOAuthToken("3e94b3e7b117c36cbc097f37e0a9233d605c5be9").build();

//        github.getRepository()

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
