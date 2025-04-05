package org.rococo.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.mapper.UserMapper;
import org.rococo.gateway.model.users.UserShortDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping({"/api/user", "/api/user/"})
@RequiredArgsConstructor
public class UsersController {

    private final UsersGrpcClient usersClient;

    @GetMapping("/all")
    public Page<UserShortDTO> getAllUsers(@PageableDefault(size = 20, sort = {"username"}) Pageable pageable) {
        log.info("Find all users");
        var usersPage = usersClient.findAll(pageable);
        var usersShort = usersPage.getContent().stream()
                .map(UserMapper::toShortDTO)
                .toList();
        return new PageImpl<>(usersShort, pageable, usersPage.getTotalElements());
    }

}
