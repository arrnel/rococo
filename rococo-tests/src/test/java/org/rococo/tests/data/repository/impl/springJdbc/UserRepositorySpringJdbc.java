package org.rococo.tests.data.repository.impl.springJdbc;

import org.rococo.tests.data.dao.UserDao;
import org.rococo.tests.data.dao.impl.springJdbc.UserDaoSpringJdbc;
import org.rococo.tests.data.entity.UserEntity;
import org.rococo.tests.data.repository.UserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserRepositorySpringJdbc implements UserRepository {

    private final UserDao userdataUserDao = new UserDaoSpringJdbc();

    @Override
    public @Nonnull UserEntity create(UserEntity user) {
        return userdataUserDao.create(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        return userdataUserDao.findById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {
        return userdataUserDao.findByUsername(username);
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return userdataUserDao.findAll();
    }

    @Override
    public @Nonnull UserEntity update(UserEntity user) {
        return userdataUserDao.update(user);
    }

    @Override
    public void remove(UserEntity user) {
        userdataUserDao.remove(user);
    }

    public void removeAll() {
        userdataUserDao.removeAll();
    }

}
