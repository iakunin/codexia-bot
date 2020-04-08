package dev.iakunin.codexiabot.hackernews.service;

import dev.iakunin.codexiabot.hackernews.entity.HackernewsItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("hackernews.service.Composite")
@Primary
@Slf4j
@AllArgsConstructor(onConstructor_={@Autowired})
public final class Composite implements Writer {

    private final List<Writer> writerList;

    @Override
    public void write(HackernewsItem item) {
        this.writerList.forEach(writer -> writer.write(item));
    }
}
