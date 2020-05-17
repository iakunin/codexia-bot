package dev.iakunin.codexiabot.github.cron;

import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor
public class DeleteObsoleteStat implements Runnable {

    private final GithubRepoRepository repoRepository;

    private final GithubRepoStatRepository statRepository;

    private final Runner runner;

    @Override
    @Transactional
    public void run() {
        this.repoRepository
            .getAll()
            .flatMap(
                repo -> Arrays.stream(GithubRepoStat.Type.values())
                    .map(type -> new Tuple2<>(repo, type))
            )
            .map(
                tuple -> this.statRepository
                    .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                        tuple._1(),
                        tuple._2(),
                        0L
                    )
            )
            .flatMap(this::withoutFirstAndLast)
            .map(
                stat -> Try.run(() -> this.runner.run(stat))
            )
            .filter(Try::isFailure)
            .forEach(
                tr -> log.debug("Unable to delete stat ", tr.getCause())
            );
    }

    @Slf4j
    @AllArgsConstructor
    @Service
    public static class Runner {

        private final GithubRepoStatRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void run(GithubRepoStat item) {
            log.debug("Deleting item with id={}", item.getId());
            this.repository.delete(item);
            log.debug("Successfully deleted item with id={}", item.getId());
        }
    }

    private <T> Stream<T> withoutFirstAndLast(List<T> list) {
        if (list.size() <= 2) return Stream.empty();

        return list.stream()
            .skip(1L)
            .limit(list.size() - 2L);
    }
}
