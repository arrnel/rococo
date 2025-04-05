package org.rococo.tests.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(schema = "rococo", name = "museums")
public class MuseumEntity {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @ToString.Include
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @ToString.Include
    @Column(name = "description")
    private String description;

    @ToString.Include
    @Column(name = "country_id")
    private UUID countryId;

    @ToString.Include
    @Column(name = "city")
    private String city;

}
