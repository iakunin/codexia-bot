package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.bot.repository.StarsUpResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat.GithubApi;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Deque;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public class StarsUp {

    private static final Bot.Type BOT_TYPE = Bot.Type.STARS_UP;

    private final GithubModule githubModule;

    private final CodexiaModule codexiaModule;

    private final StarsUpResultRepository starsUpResultRepository;

    @Scheduled(cron="${app.cron.bot.stars-up:-}")
    public void run() {
        log.info("Running {}", this.getClass().getName());

        this.githubModule.findAllInCodexia()
            .stream()
            .map(
                repo -> Pair.with(repo, this.getLastProcessedStatId(repo))
            )
            .map(
                pair -> this.githubModule.findAllGithubApiStat(
                    pair.getValue0(),
                    pair.getValue1()
                )
            )
            .filter(statList -> statList.size() >= 2)
            .filter(
                statList -> this.shouldReviewBeSubmitted(
                    (GithubApi) statList.getFirst().getStat(),
                    (GithubApi) statList.getLast().getStat()
                )
            )
            .forEach(this::processStatList);

        log.info("Exiting from {}", this.getClass().getName());
    }

    private Long getLastProcessedStatId(GithubRepo repo) {
        return this.starsUpResultRepository
            .findFirstByGithubRepoOrderByIdDesc(repo)
            .map(
                starsUpResult -> starsUpResult.getGithubRepoStat().getId()
            )
            .orElse(0L);
    }

    // @todo #6 add test case with transaction rollback
    @Transactional
    protected void processStatList(Deque<GithubRepoStat> deque) {
        final CodexiaReview review = this.createReview(deque.getFirst(), deque.getLast());

        this.starsUpResultRepository.save(
            new StarsUpResult()
                .setGithubRepo(deque.getLast().getGithubRepo())
                .setGithubRepoStat(deque.getLast())
        );
        this.codexiaModule.sendReview(review);
        this.codexiaModule.sendMeta(
            review.getCodexiaProject(),
            "stars-count",
            this.codexiaModule
                .findAllReviews(review.getCodexiaProject(), review.getAuthor())
                .stream()
                .map(CodexiaReview::getReason)
                .collect(Collectors.joining(","))
        );
    }

    private CodexiaReview createReview(GithubRepoStat first, GithubRepoStat last) {
        final GithubApi firstStat = (GithubApi) first.getStat();
        final GithubApi lastStat = (GithubApi) last.getStat();

        return new CodexiaReview()
            .setText(
                String.format(
                    "The repo gained %d stars since %s (was: %d stars, now: %d stars). " +
                    "See the stars history [here](https://star-history.t9t.io/#%s).",
                    lastStat.getStars() - firstStat.getStars(),
                    ZonedDateTime.of(first.getCreatedAt(), ZoneOffset.UTC).toString(),
                    firstStat.getStars(),
                    lastStat.getStars(),
                    first.getGithubRepo().getFullName()
                )
            )
            .setAuthor(BOT_TYPE.name())
            .setReason(String.valueOf(lastStat.getStars()))
            .setCodexiaProject(this.getCodexiaProject(last));
    }

    private CodexiaProject getCodexiaProject(GithubRepoStat last) {
        return this.codexiaModule
            .findCodexiaProject(last.getGithubRepo())
            .orElseThrow(
                () -> new RuntimeException(
                    String.format(
                        "Unable to find CodexiaProject for githubRepoId='%s'",
                        last.getGithubRepo().getId()
                    )
                )
            );
    }

    private boolean shouldReviewBeSubmitted(GithubApi first, GithubApi last) {
        final int increase = last.getStars() - first.getStars();

        if (increase < 10) {
            return false;
        }

        return increase >= (first.getStars() * 0.05);
    }
}
