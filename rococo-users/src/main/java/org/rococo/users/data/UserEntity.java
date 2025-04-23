package org.rococo.users.data;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(schema = "rococo", name = "users")
public class UserEntity {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ToString.Include
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @ToString.Include
    @Column(name = "first_name")
    private String firstName;

    @ToString.Include
    @Column(name = "last_name")
    private String lastName;

    @ToString.Include
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

}
