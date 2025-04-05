package org.rococo.tests.mapper;

import org.rococo.tests.data.entity.AuthUserEntity;
import org.rococo.tests.data.entity.AuthorityEntity;
import org.rococo.tests.model.AuthorityDTO;

public class AuthorityMapper {

    public static AuthorityEntity toEntity(AuthorityDTO dto) {
        return AuthorityEntity.builder()
                .id(dto.getId())
                .user(AuthUserEntity.builder()
                        .id(dto.getUserId())
                        .build())
                .authority(dto.getAuthority())
                .build();
    }

    public static AuthorityDTO toDTO(AuthorityEntity entity) {
        return AuthorityDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .authority(entity.getAuthority())
                .build();
    }

}
