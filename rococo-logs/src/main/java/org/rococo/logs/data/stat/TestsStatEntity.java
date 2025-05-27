package org.rococo.logs.data.stat;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Entity
@Table(schema = "rococo", name = "tests_stats")
public class TestsStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "failed", nullable = false)
    private Integer failed;

    @Column(name = "broken", nullable = false)
    private Integer broken;

    @Column(name = "skipped", nullable = false)
    private Integer skipped;

    @Column(name = "passed", nullable = false)
    private Integer passed;

    @Column(name = "unknown", nullable = false)
    private Integer unknown;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Column(name = "is_passed", nullable = false, updatable = false)
    private Boolean isPassed;

    @Column(name = "passed_percentage", nullable = false, updatable = false)
    private Double passedPercentage;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TestsStatEntity that = (TestsStatEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
