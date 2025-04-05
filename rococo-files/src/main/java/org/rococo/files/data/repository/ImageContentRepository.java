package org.rococo.files.data.repository;

import org.rococo.files.data.entity.ImageContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageContentRepository extends JpaRepository<ImageContentEntity, UUID> {
}
