package org.rococo.tests.model.allure;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScreenDiff {

    private String expected;
    private String actual;
    private String diff;

}