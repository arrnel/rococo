package org.rococo.files.config;

public class AppProperty {

    private AppProperty() {
    }

    public static final int MIN_IMAGE_WIDTH = 100;
    public static final int MIN_IMAGE_HEIGHT = 100;
    public static final double QUALITY = 1.0;
    public static final String IMAGE_PATTERN = "^data:image\\/[a-z]{3,4};base64,[A-Za-z0-9+/]+={0,2}$";

}
