package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;
import org.rococo.tests.client.gateway.GitHubApiClient;
import org.rococo.tests.jupiter.annotation.DisabledByIssue;

import static org.rococo.tests.client.gateway.GitHubApiClient.IssueState.OPEN;

public class IssueExtension implements ExecutionCondition {

    private static final GitHubApiClient gitHubApiClient = new GitHubApiClient();

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                DisabledByIssue.class
        ).or(
                () -> AnnotationSupport.findAnnotation(
                        context.getRequiredTestClass(),
                        DisabledByIssue.class,
                        SearchOption.INCLUDE_ENCLOSING_CLASSES
                )
        ).map(
                byIssue -> OPEN.equals(gitHubApiClient.getIssueState(byIssue.issueId()))
                        ? ConditionEvaluationResult.disabled("Disabled by issue #" + byIssue.issueId())
                        : ConditionEvaluationResult.enabled("Issue closed")
        ).orElseGet(
                () -> ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
        );
    }
}
