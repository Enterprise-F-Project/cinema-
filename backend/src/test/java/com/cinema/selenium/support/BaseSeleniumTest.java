package com.cinema.selenium.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Shared WebDriver lifecycle and explicit-wait helpers for Cinema Selenium tests.
 */
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        if (SeleniumConfig.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // Selenium Manager (Selenium 4.6+) resolves ChromeDriver automatically.
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void open(String path) {
        String normalized = path.startsWith("/") ? path : "/" + path;
        driver.get(SeleniumConfig.baseUrl() + normalized);
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void waitTextPresent(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Next.js {@code <Link>} navigation is unreliable with Selenium's default center-click
     * in this UI; a DOM click reliably triggers client-side routing.
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void jsClick(By locator) {
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(locator)));
    }

    /**
     * Sets values on React controlled inputs/selects (including {@code type=date}/{@code number})
     * using the native value setter and React's {@code _valueTracker} so state stays in sync.
     */
    protected void setFieldValue(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const value = arguments[1];"
                        + "const tag = el.tagName.toLowerCase();"
                        + "let proto;"
                        + "if (tag === 'textarea') proto = window.HTMLTextAreaElement.prototype;"
                        + "else if (tag === 'select') proto = window.HTMLSelectElement.prototype;"
                        + "else proto = window.HTMLInputElement.prototype;"
                        + "const descriptor = Object.getOwnPropertyDescriptor(proto, 'value');"
                        + "const last = el.value;"
                        + "descriptor.set.call(el, value);"
                        + "if (el._valueTracker) { el._valueTracker.setValue(last); }"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));",
                element,
                value);
    }

    protected void selectByVisibleText(By locator, String visibleText) {
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        String value = (String) ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const text = arguments[1];"
                        + "const opt = Array.from(el.options).find(o => (o.textContent || '').trim() === text);"
                        + "return opt ? opt.value : null;",
                select,
                visibleText);
        if (value == null) {
            throw new IllegalStateException("No <option> with visible text: " + visibleText);
        }
        setFieldValue(locator, value);
    }
}
