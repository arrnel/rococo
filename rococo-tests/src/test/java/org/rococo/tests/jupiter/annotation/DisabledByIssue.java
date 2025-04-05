package org.rococo.tests.jupiter.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.tests.jupiter.extension.IssueExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(IssueExtension.class)
public @interface DisabledByIssue {
    String issueId() default "";
}