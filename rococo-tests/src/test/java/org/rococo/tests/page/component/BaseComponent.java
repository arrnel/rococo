package org.rococo.tests.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseComponent<T extends BaseComponent<T>> {

    protected final SelenideElement self;

    public abstract T shouldVisibleComponent();

    public abstract void shouldNotVisibleComponent();

}
