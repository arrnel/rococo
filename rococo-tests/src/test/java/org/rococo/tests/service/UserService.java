package org.rococo.tests.service;

import org.rococo.tests.model.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDTO create(UserDTO userDTO);

    Optional<UserDTO> findById(UUID id);

    Optional<UserDTO> findByUsername(String username);

    List<UserDTO> findAll();

    UserDTO update(UserDTO userDTO);

    void delete(String username);

    void clearAll();

}
