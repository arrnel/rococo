package org.rococo.logs.data.log;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;
import org.rococo.logs.model.ServiceName;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(schema = "rococo", name = "logs", indexes = {
        @Index(name = "idx_logs_service", columnList = "service"),
        @Index(name = "idx_logs_time", columnList = "time")
})
public class LogEntity {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false, updatable = false)
    private ServiceName serviceName;

    @ToString.Include
    @Column(name = "time", nullable = false, updatable = false)
    private LocalDateTime time;

    @Column(name = "message", nullable = false, updatable = false)
    private String message;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LogEntity logEntity = (LogEntity) o;
        return uuid != null && Objects.equals(uuid, logEntity.uuid);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
