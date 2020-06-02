package dev.iakunin.codexiabot.github.cron;

import dev.iakunin.codexiabot.common.runnable.FaultTolerant;
import dev.iakunin.codexiabot.github.entity.GithubRepo;
import dev.iakunin.codexiabot.github.entity.GithubRepoStat;
import dev.iakunin.codexiabot.github.repository.GithubRepoRepository;
import dev.iakunin.codexiabot.github.repository.GithubRepoStatRepository;
import io.vavr.Tuple2;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @checkstyle DesignForExtension (500 lines)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public final class DeleteObsoleteStat implements Runnable {

    private static final int PAGE_SIZE = 500;

    private final GithubRepoRepository repo;

    private final GithubRepoStatRepository stat;

    private final Runner runner;

    @Override
    public void run() {
        Page<GithubRepo> page;
        int num = 0;
        do {
            page = this.repo.getAll(PageRequest.of(num, PAGE_SIZE));
            new FaultTolerant(
                page
                    .stream()
                    .flatMap(
                        rep -> Arrays.stream(GithubRepoStat.Type.values())
                            .map(type -> new Tuple2<>(rep, type))
                    )
                    .map(
                        tuple -> this.stat
                            .findAllByGithubRepoAndTypeAndIdGreaterThanEqualOrderByIdAsc(
                                tuple._1(),
                                tuple._2(),
                                0L
                            ).stream()
                    )
                    .flatMap(this::withoutFirstAndLast)
                    .map(st -> () -> this.runner.run(st)),
                tr -> log.debug("Unable to delete stat: {}", tr.getCause().getMessage())
            ).run();
            num += 1;
        } while (page.hasNext());
    }

    private <T> Stream<T> withoutFirstAndLast(final Stream<T> stream) {
        final List<T> list = stream.collect(Collectors.toList());

        return list.size() <= 2
            ? Stream.empty()
            : list.stream()
                .skip(1L)
                .limit(list.size() - 2L);
    }

    @Slf4j
    @RequiredArgsConstructor
    @Service
    public static class Runner {

        private final GithubRepoStatRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void run(final GithubRepoStat item) {
            log.debug("Deleting item with id={}", item.getId());
            this.repository.delete(item);
            log.debug("Successfully deleted item with id={}", item.getId());
        }
    }
}
