package com.cinema.selenium.support;

/**
 * Runtime configuration for Selenium system tests.
 * Values are read from system properties or environment variables — no machine-specific paths.
 */
public final class SeleniumConfig {

    public static final String DEFAULT_BASE_URL = "http://localhost:3000";

    private SeleniumConfig() {
    }

    public static String baseUrl() {
        String fromProp = System.getProperty("selenium.baseUrl");
        if (fromProp != null && !fromProp.isBlank()) {
            return trimTrailingSlash(fromProp.trim());
        }
        String fromEnv = System.getenv("SELENIUM_BASE_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return trimTrailingSlash(fromEnv.trim());
        }
        return DEFAULT_BASE_URL;
    }

    public static boolean headless() {
        String fromProp = System.getProperty("selenium.headless");
        if (fromProp != null) {
            return Boolean.parseBoolean(fromProp);
        }
        String fromEnv = System.getenv("SELENIUM_HEADLESS");
        return fromEnv != null && Boolean.parseBoolean(fromEnv);
    }

    public static String demoPassword() {
        return "Password123!";
    }

    public static String clientEmail() {
        return "client@cinema.com";
    }

    public static String distributorEmail() {
        return "distributor@cinema.com";
    }

    public static String adminEmail() {
        return "admin@cinema.com";
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
