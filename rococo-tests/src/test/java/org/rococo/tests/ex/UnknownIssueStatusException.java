package org.rococo.tests.ex;

public class UnknownIssueStatusException extends RuntimeException {

    public UnknownIssueStatusException(String status) {
        super("Unknown issue status = [" + status + "]");
    }

}
