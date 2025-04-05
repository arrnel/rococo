package org.rococo.gateway.config;

public class AppProperty {

    private AppProperty() {
    }

    public static final String USERNAME_PATTERN = "(?i)^[a-z0-9](?:[a-z0-9]|(?<!\\.)\\.(?!\\.)){4,28}[a-z0-9]$";
    public static final String IMAGE_PATTERN = "^data:image\\/[a-z]{3,4};base64,([A-Za-z0-9+/]+={0,2})$";

}
