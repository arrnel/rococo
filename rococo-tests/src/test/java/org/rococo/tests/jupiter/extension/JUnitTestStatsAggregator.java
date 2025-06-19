package org.rococo.tests.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EXTENSION HAS GLOBAL REGISTRATION TYPE
 */
@ParametersAreNonnullByDefault
public class JUnitTestStatsAggregator implements SuiteExtension, TestWatcher {

    private static final AtomicInteger PASSED = new AtomicInteger(0);
    private static final AtomicInteger FAILED = new AtomicInteger(0);
    private static final AtomicInteger BROKEN = new AtomicInteger(0);
    private static final AtomicInteger SKIPPED = new AtomicInteger(0);
    private static final AtomicInteger TOTAL = new AtomicInteger(0);

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (isAssertionError(cause)) {
            FAILED.incrementAndGet();
        } else {
            BROKEN.incrementAndGet();
        }
        TOTAL.incrementAndGet();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        PASSED.incrementAndGet();
        TOTAL.incrementAndGet();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        SKIPPED.incrementAndGet();
        TOTAL.incrementAndGet();
    }

    private boolean isAssertionError(Throwable throwable) {
        return throwable instanceof AssertionError
                || (throwable.getCause() != null && isAssertionError(throwable.getCause()));
    }

    public static Integer getFailedTestsCount() {
        return FAILED.get();
    }

    public static Integer getPassedTestCount() {
        return PASSED.get();
    }

    public static Integer getBrokenTestCount() {
        return BROKEN.get();
    }

    public static Integer getTotalTestCount() {
        return TOTAL.get();
    }

    public static Integer getSkippedTestCount() {
        return SKIPPED.get();
    }

}
