package com.vv.export.sandbox.util;

import java.util.Properties;

import static com.vv.export.sandbox.util.Utility.DEFAULT_VALUE_STRING;

public class Helper {

    public static Properties properties;

    public static String getProperty(String key, String def) {
        if (properties.containsKey(key)) return properties.getProperty(key);
        return def;
    }

    public static String getProperty(String key) {
        return getProperty(key, DEFAULT_VALUE_STRING);
    }

}
