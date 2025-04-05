package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.FilesGrpcClient;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.ex.BadRequestException;
import org.rococo.gateway.ex.CurrentUserNotFoundException;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.rococo.gateway.model.EntityType.*;
import static org.rococo.gateway.model.EntityType.ARTIST;

@Slf4j
@RestController
@RequestMapping({"/api/user", "/api/user/"})
@RequiredArgsConstructor
public class UserController {

    private final UsersGrpcClient usersClient;
    private final FilesGrpcClient filesGrpcClient;

    @ModelAttribute("user")
    public UserDTO getUser(@AuthenticationPrincipal Jwt jwt) {

        final String username = jwt.getClaimAsString("sub");

        var user = usersClient.findByUsername(username)
                .orElseThrow(() -> new CurrentUserNotFoundException(username));

        filesGrpcClient.findImage(USER, user.getId())
                .ifPresent(image -> user.setPhoto(image.content()));

        return user;
    }

    @GetMapping
    public UserDTO getCurrentUser(@ModelAttribute("user") UserDTO user) {
        log.info("Find user by username: {}", user.getUsername());
        return user;
    }

    @PatchMapping
    public UserDTO updateCurrentUser(@ModelAttribute("user") UserDTO user,
                                     @RequestBody UpdateUserRequestDTO requestDTO,
                                     BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getFieldErrors());

        log.info("Update user by username = [{}] and request: {}", user.getUsername(), requestDTO);

        // If photo exists in request -> Update photo if exists in rococo-files service, else add new photo
        // If photo not exists in request -> remove photo from rococo-files service
        Optional.ofNullable(requestDTO.photo())
                .ifPresentOrElse(
                        photo -> filesGrpcClient.findImage(USER, user.getId())
                                .ifPresentOrElse(image -> filesGrpcClient.update(PAINTING, user.getId(), photo),
                                        () -> filesGrpcClient.add(USER, user.getId(), photo)),
                        () -> filesGrpcClient.delete(USER, user.getId()));

        return usersClient.update(user.getId(), requestDTO);
    }

}
