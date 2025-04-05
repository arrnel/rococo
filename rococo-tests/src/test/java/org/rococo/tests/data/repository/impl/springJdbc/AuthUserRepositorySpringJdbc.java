package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.AuthUserDao;
import org.rococo.tests.data.dao.AuthorityDao;
import org.rococo.tests.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import org.rococo.tests.data.dao.impl.springJdbc.AuthorityDaoSpringJdbc;
import org.rococo.tests.data.entity.AuthUserEntity;
import org.rococo.tests.data.entity.AuthorityEntity;
import org.rococo.tests.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthorityDao authorityDao = new AuthorityDaoSpringJdbc();

    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        var createdUser = authUserDao.create(user);
        authorityDao.create(
                user.getAuthorities().stream()
                        .map(authorityEntity -> authorityEntity.setUser(createdUser))
                        .toArray(AuthorityEntity[]::new));
        return createdUser
                .setAuthorities(authorityDao.findByUserId(createdUser.getId()));
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return authUserDao.findById(id)
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())));
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return authUserDao.findByUsername(username)
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())));
    }

    @Nonnull
    @Override
    public List<AuthUserEntity> findAll() {
        return authUserDao.findAll()
                .stream()
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())))
                .toList();
    }

    @Nonnull
    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        authorityDao.update(
                user.getAuthorities()
                        .toArray(AuthorityEntity[]::new));
        return authUserDao.update(
                user.setAuthorities(
                        authorityDao.findByUserId(user.getId())));
    }

    @Override
    public void remove(AuthUserEntity user) {
        authorityDao.remove(
                authorityDao.findByUserId(user.getId())
                        .toArray(AuthorityEntity[]::new));
        authUserDao.remove(user);
    }

    @Override
    public void removeAll() {
        authorityDao.removeAll();
        authUserDao.removeAll();
    }

}