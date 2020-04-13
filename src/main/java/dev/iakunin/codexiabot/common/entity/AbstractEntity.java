package dev.iakunin.codexiabot.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import org.hibernate.annotations.UpdateTimestamp;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractEntity {
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
    // @todo #6 choose better created_at postgres-format (with or without TZ).
    //  Also think of ZonedDateTime in createdAt field.
    //  ZonedDateTime + timestamptz seems the best way.
    //  - Test changing of postgres server timezone.
    //  - Test changing of hibernate.time_zone property.
    //  - Test changing of JVM timezone.
    //  In all of these cases there MUST be valid timestamptz in postgres.
    //  In all of these cases there MUST be valid ZonedDateTime in code.

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersistFunction(){
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
