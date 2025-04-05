package org.rococo.tests.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class InvalidGitHubCredentials extends RuntimeException {

    public InvalidGitHubCredentials(final String message) {
        super(message);
    }

}
