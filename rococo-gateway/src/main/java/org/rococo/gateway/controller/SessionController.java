package org.rococo.gateway.controller;

import org.rococo.gateway.model.Session;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping
    public Session session(@AuthenticationPrincipal Jwt principal) {
        // @formatter:off
        return (principal == null)
                ? Session.empty()
                : Session.builder()
                        .username(principal.getClaimAsString("sub"))
                        .issuedAt(Date.from(principal.getIssuedAt()))
                        .expiresAt(Date.from(principal.getExpiresAt()))
                        .build();
        // @formatter:on
    }

}
