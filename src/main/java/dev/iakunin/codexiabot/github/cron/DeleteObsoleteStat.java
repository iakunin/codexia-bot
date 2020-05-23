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

@Component
@Slf4j
@RequiredArgsConstructor
public final class DeleteObsoleteStat implements Runnable {

    private final GithubRepoRepository repoRepository;

    private final GithubRepoStatRepository statRepository;

    private final Runner runner;

    @Override
    public void run() {
        Page<GithubRepo> page;
        int pageNum = 0;
        do {
            page = this.repoRepository.getAll(PageRequest.of(pageNum, 500));
            new FaultTolerant(
                page
                    .stream()
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
                            ).stream()
                    )
                    .flatMap(this::withoutFirstAndLast)
                    .map(stat -> () -> this.runner.run(stat)),
                tr -> log.debug("Unable to delete stat: {}", tr.getCause().getMessage())
            ).run();
            pageNum++;
        } while (page.hasNext());
    }

    @Slf4j
    @RequiredArgsConstructor
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

    private <T> Stream<T> withoutFirstAndLast(Stream<T> stream) {
        final List<T> list = stream.collect(Collectors.toList());
        if (list.size() <= 2) return Stream.empty();

        return list.stream()
            .skip(1L)
            .limit(list.size() - 2L);
    }
}
