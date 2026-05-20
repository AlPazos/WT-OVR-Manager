package com.pazos.wtovrmanager;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class I18n {

    private static ResourceBundle bundle;

    public static void init(ResourceBundle b) {
        bundle = b;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String get(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }
}