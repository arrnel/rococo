package org.rococo.tests.mapper;

import org.rococo.tests.data.entity.AuthUserEntity;
import org.rococo.tests.model.AuthUserDTO;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AuthUserMapper {

    @Nonnull
    public static AuthUserEntity toEntity(AuthUserDTO dto) {
        var authUser = AuthUserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .accountNonExpired(dto.isAccountNonExpired())
                .accountNonLocked(dto.isAccountNonLocked())
                .credentialsNonExpired(dto.isCredentialsNonExpired())
                .enabled(dto.isEnabled())
                .authorities(
                        dto.getAuthorities().stream()
                                .map(AuthorityMapper::toEntity)
                                .toList()
                )
                .build();
        authUser.getAuthorities()
                .forEach(a -> a.setUser(authUser));
        return authUser;
    }

    @Nonnull
    public static AuthUserDTO toDTO(AuthUserEntity authUserEntity) {
        return AuthUserDTO.builder()
                .id(authUserEntity.getId())
                .username(authUserEntity.getUsername())
                .enabled(authUserEntity.getEnabled())
                .accountNonExpired(authUserEntity.getAccountNonExpired())
                .accountNonLocked(authUserEntity.getAccountNonLocked())
                .enabled(authUserEntity.getEnabled())
                .credentialsNonExpired(authUserEntity.getCredentialsNonExpired())
                .password(authUserEntity.getPassword())
                .authorities(
                        authUserEntity.getAuthorities().stream()
                                .map(AuthorityMapper::toDTO)
                                .toList())
                .build();
    }

}