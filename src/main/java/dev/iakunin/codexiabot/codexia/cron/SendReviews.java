package dev.iakunin.codexiabot.codexia.cron;

import dev.iakunin.codexiabot.codexia.repository.CodexiaReviewRepository;
import dev.iakunin.codexiabot.codexia.service.ReviewSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_={@Autowired})
public class SendReviews implements Runnable {

    private final CodexiaReviewRepository codexiaReviewRepository;

    private final ReviewSender reviewSender;

    @Transactional
    public void run() {
        this.codexiaReviewRepository
            .findAllWithoutNotifications()
            .forEach(this.reviewSender::send);
    }
}
