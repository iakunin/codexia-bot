package com.iakunin.codexiabot.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class)
})
@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
abstract public class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(updatable = false)
    @EqualsAndHashCode.Include
    private UUID uuid;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersistFunction(){
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
