package org.rococo.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.ex.BadRequestException;
import org.rococo.gateway.ex.CurrentUserNotFoundException;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping({"/api/user", "/api/user/"})
@RequiredArgsConstructor
public class UserController {

    private final UsersGrpcClient usersClient;

    @ModelAttribute("user")
    public UserDTO getUser(@AuthenticationPrincipal Jwt jwt) {
        final String username = jwt.getClaimAsString("sub");
        return usersClient.findByUsername(username)
                .orElseThrow(() -> new CurrentUserNotFoundException(username));
    }

    @GetMapping
    public UserDTO getCurrentUser(@ModelAttribute("user") UserDTO user) {
        log.info("Find user by username: {}", user.getUsername());
        return user;
    }

    @PatchMapping
    public UserDTO updateCurrentUser(@ModelAttribute("user") UserDTO user,
                                     @Valid @RequestBody UpdateUserRequestDTO requestDTO,
                                     BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getFieldErrors());
        log.info("Update user by username = [{}] and request: {}", user.getUsername(), requestDTO);
        return usersClient.update(user.getId(), requestDTO);
    }

}
