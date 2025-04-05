package org.rococo.tests.service.db;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.entity.AuthorityEntity;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.entity.UserEntity;
import org.rococo.tests.data.repository.AuthUserRepository;
import org.rococo.tests.data.repository.FilesRepository;
import org.rococo.tests.data.repository.UserRepository;
import org.rococo.tests.data.repository.impl.springJdbc.AuthUserRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.FilesRepositorySpringJdbc;
import org.rococo.tests.data.repository.impl.springJdbc.UserRepositorySpringJdbc;
import org.rococo.tests.data.tpl.XaTransactionTemplate;
import org.rococo.tests.enums.Authority;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.mapper.AuthUserMapper;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.mapper.UserMapper;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.ARTIST;
import static org.rococo.tests.enums.EntityType.USER;

@Slf4j
@SuppressWarnings("unchecked")
public class UserServiceDb implements UserService {

    private static final Config CFG = Config.getInstance();

    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();
    private final UserRepository userRepository = new UserRepositorySpringJdbc();
    private final FilesRepository filesRepository = new FilesRepositorySpringJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.usersJdbcUrl(), CFG.filesJdbcUrl());

    @Override
    @Step("Create new user: [{user.username}]")
    public UserDTO create(UserDTO user) {

        log.info("Create new user: {}", user);

        var userPassword = user.getTestData().getPassword();
        var authUserEntity = AuthUserMapper.toEntity(UserMapper.toAuthDTO(user));
        authUserEntity.setAuthorities(
                List.of(AuthorityEntity.builder().authority(Authority.read).user(authUserEntity).build(),
                        AuthorityEntity.builder().authority(Authority.write).user(authUserEntity).build())
        );

        return xaTxTemplate.execute(() -> {

            authUserRepository.create(authUserEntity);
            var newUser = UserMapper.toDTO(
                    userRepository.create(
                            UserMapper.toEntity(user)));

            Optional.ofNullable(user.getPhoto())
                    .ifPresent(photo -> {
                        var imageMetadata = filesRepository.create(
                                ImageMapper.fromBase64Image(USER, newUser.getId(), photo));
                        newUser.setPhoto(new String(
                                imageMetadata.getContent().getData(),
                                StandardCharsets.UTF_8));
                    });

            return newUser.password(userPassword);

        });

    }

    @Override
    @Step("Find user by id: [{id}]")
    public Optional<UserDTO> findById(UUID id) {
        log.info("Find user with id: {}", id);
        return xaTxTemplate.execute(() ->
                userRepository.findById(id)
                        .map(this::enrichAndConvertToDTO));
    }

    @Override
    @Step("Find user by username: [{username}]")
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Find user by username: {}", username);
        return xaTxTemplate.execute(() ->
                userRepository.findByUsername(username)
                        .map(this::enrichAndConvertToDTO));
    }

    @Override
    @Step("Find all users")
    public List<UserDTO> findAll() {

        log.info("Find all users");

        return xaTxTemplate.execute(() -> {
            var users = userRepository.findAll();
            var usersImagesMap = filesRepository.findAllByEntityTypeAndEntityIds(
                            USER,
                            users.stream()
                                    .map(UserEntity::getId)
                                    .toList())
                    .stream()
                    .collect(Collectors.toMap(ImageMetadataEntity::getEntityId, u -> u.getContent().getData()));

            return users.stream()
                    .map(user -> UserMapper.toDTO(user, usersImagesMap.get(user.getId())))
                    .toList();
        });

    }

    @Override
    @Step("Update user with id: [{user.id}]")
    public UserDTO update(UserDTO user) {

        log.info("Update user: {}", user);

        return xaTxTemplate.execute(() -> {

            var userEntity = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ArtistNotFoundException(user.getId()));

            var photo = filesRepository.findByEntityTypeAndEntityId(USER, user.getId())
                    .map(oldMetadata -> {
                                var newMetadata = ImageMapper.fromBase64Image(USER, user.getId(), user.getPhoto());
                                if (!oldMetadata.getContentHash().equals(newMetadata.getContentHash()))
                                    newMetadata = filesRepository.update(newMetadata);
                                return newMetadata.getContent().getData();
                            }
                    ).orElse(null);

            var updatedFromDtoEntity = UserMapper.updateFromDTO(userEntity, user);

            return UserMapper.toDTO(
                    userRepository.update(updatedFromDtoEntity),
                    photo);

        });

    }

    @Override
    @Step("Delete user with username: [{username}]")
    public void delete(String username) {
        log.info("Delete user with username: {}", username);
        xaTxTemplate.execute(() -> {
            userRepository.findByUsername(username)
                    .ifPresent(user -> {
                        filesRepository.findByEntityTypeAndEntityId(ARTIST, user.getId())
                                .ifPresent(filesRepository::remove);
                        userRepository.remove(user);
                    });
            return null;
        });
    }

    @Override
    @Step("Truncate table \"rococo-users\" and remove all files with entity_type USER from \"rococo-files\"")
    public void clearAll() {
        log.info("Truncate table \"rococo-users\" and remove all files with entity_type USER from \"rococo-files\"");
        xaTxTemplate.execute(() -> {
            userRepository.removeAll();
            authUserRepository.removeAll();
            filesRepository.removeAll(ARTIST);
            return null;
        });
    }

    private UserDTO enrichAndConvertToDTO(UserEntity user) {
        var imageMetadata = filesRepository.findByEntityTypeAndEntityId(USER, user.getId());
        return UserMapper.toDTO(user)
                .setPhoto(imageMetadata
                        .map(metadata ->
                                new String(metadata.getContent().getData(), StandardCharsets.UTF_8))
                        .orElse(null));
    }

}
