package dev.iakunin.codexiabot.bot;

import dev.iakunin.codexiabot.bot.entity.StarsUpResult;
import dev.iakunin.codexiabot.bot.repository.StarsUpResultRepository;
import dev.iakunin.codexiabot.codexia.CodexiaModule;
import dev.iakunin.codexiabot.codexia.entity.CodexiaReview;
import dev.iakunin.codexiabot.github.GithubModule;
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
                repo -> Pair.with(
                    repo,
                    this.starsUpResultRepository.findFirstByGithubRepoOrderByIdDesc(repo)
                )
            )
            .map(
                pair -> Pair.with(
                    pair.getValue0(),
                    pair.getValue1().map(
                        result -> result.getGithubRepoStat().getId()
                    ).orElse(0L)
                )
            )
            .map(
                pair -> this.githubModule.findAllGithubApiStat(pair.getValue0(), pair.getValue1())
            )
            .filter(statList -> statList.size() >= 2)
            .filter(
                statList -> this.shouldReviewBeSubmitted(
                    (GithubApi) statList.getFirst().getStat(),
                    (GithubApi) statList.getLast().getStat()
                )
            )
            .forEach(this::processStatList)
        ;

        // Тест-кейс, когда githubModule.findAllGithubApiStat возвращает больше 2х записей
        // Тест-кейс, когда githubModule.findAllGithubApiStat возвращает точно 2 записи
        // Тест-кейс, когда githubModule.findAllGithubApiStat возвращает меньше 2х записей

        // Тест-кейсы с двумя последовательными запусками с изменениями состояния бота (бот сработал)
        // Тест-кейсы с двумя последовательными запусками с изменениями состояния github-статистики
        //    (бот может как сработать, так и нет)

        // - Assert the full review text (including timezone!)


        log.info("Exiting from {}", this.getClass().getName());
    }

    @Transactional
    protected void processStatList(Deque<GithubRepoStat> deque) {
        final GithubRepoStat first = deque.getFirst();
        final GithubRepoStat last = deque.getLast();
        final GithubApi firstStat = (GithubApi) first.getStat();
        final GithubApi lastStat = (GithubApi) last.getStat();

        final CodexiaReview review = new CodexiaReview()
            .setText(
                String.format(
                    "The repo gained %d stars since %s " +
                    "(was: %d stars, now: %d stars). " +
                    "See the stars history [here](https://star-history.t9t.io/#%s).",
                    lastStat.getStars() - firstStat.getStars(),
                    ZonedDateTime.of(
                        first.getCreatedAt(),
                        ZoneOffset.UTC
                    ).toString(),
                    firstStat.getStars(),
                    lastStat.getStars(),
                    first.getGithubRepo().getFullName()
                )
            )
            .setAuthor(BOT_TYPE.name())
            .setReason(String.valueOf(lastStat.getStars()))
            .setCodexiaProject(
                this.codexiaModule
                    .findCodexiaProject(last.getGithubRepo())
                    .orElseThrow(
                        () -> new RuntimeException(
                            String.format(
                                "Unable to find CodexiaProject for githubRepoId='%s'",
                                last.getGithubRepo().getId()
                            )
                        )
                    )
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
        this.starsUpResultRepository.save(
            new StarsUpResult()
                .setGithubRepo(last.getGithubRepo())
                .setGithubRepoStat(last)
        );
    }

    private boolean shouldReviewBeSubmitted(GithubApi firstStat, GithubApi lastStat) {
        final int increase = lastStat.getStars() - firstStat.getStars();

        if (increase < 10) {
            log.info(
                "Increase is smaller than 10; increase={}; firstStat={}; lastStat={};",
                increase,
                firstStat,
                lastStat
            );
            return false;
        }

        if (increase >= (firstStat.getStars() * 0.05)) {
            log.info(
                "Review should be submitted; increase={}; firstStat={}; lastStat={};",
                increase,
                firstStat,
                lastStat
            );
            return true;
        }

        log.info(
            "Review should not be submitted; increase={}; firstStat={}; lastStat={};",
            increase,
            firstStat,
            lastStat
        );
        return false;
    }
}
