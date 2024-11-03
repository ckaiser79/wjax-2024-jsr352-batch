package de.continentale.vu.demo_jsr352.rest_controller;

import java.util.Map;
import java.util.Properties;

public class JobUtils {

    public static Properties toProperties(final Map<String, String> jobParameters) {
        final Properties resultProperties = new Properties();
        jobParameters.forEach((k, v) -> resultProperties.put(k, v));
        return resultProperties;
    }


    public static Properties toPropertiesFromArray(final Map<String, String[]> jobParameters) {
        final Properties resultProperties = new Properties();
        jobParameters.forEach((k, v) -> resultProperties.put(k, v[0]));
        return resultProperties;
    }

}
