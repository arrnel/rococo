package org.rococo.tests.browser;

import org.rococo.tests.config.Config;

interface BaseStrategy {

    Config CFG = Config.getInstance();

    void initDriver();

}
