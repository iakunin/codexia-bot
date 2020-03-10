package com.iakunin.codexiabot.codexia.entity;

import com.iakunin.codexiabot.common.entity.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class CodexiaReviewNotification extends AbstractEntity {

    @ManyToOne
    private CodexiaReview codexiaReview;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Integer responseCode;
    private String response;

    public enum Status {
        NEW,
        SENT,
    }
}
