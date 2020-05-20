package dev.iakunin.codexiabot.codexia.service;

import dev.iakunin.codexiabot.codexia.entity.CodexiaProject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("codexia.service.Composite")
@Primary
@Slf4j
@RequiredArgsConstructor
public final class Composite implements Writer {

    private final List<Writer> writerList;

    @Override
    public void write(CodexiaProject project) {
        this.writerList.forEach(writer -> writer.write(project));
    }
}
