package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for /licenses — selectors from licenses-page-content.tsx.
 */
public class LicensesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitle = By.xpath("//h1[normalize-space()='Licenses']");
    private final By createLicenseButton = By.xpath("//button[normalize-space()='Create license']");
    private final By movieSelect = By.id("license-movie");
    private final By clientSelect = By.id("license-client");
    private final By startDateInput = By.id("license-start");
    private final By endDateInput = By.id("license-end");
    private final By termsInput = By.id("license-terms");
    private final By submitButton = By.xpath("//button[@type='submit' and normalize-space()='Create license']");

    public LicensesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LicensesPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        return this;
    }

    public void createLicenseForMovieAndClient(
            String movieTitle,
            String clientOrganizationName,
            String startDate,
            String endDate,
            String terms) {
        WebElement openButton = wait.until(ExpectedConditions.elementToBeClickable(createLicenseButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", openButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect));

        wait.until(d -> new Select(d.findElement(movieSelect)).getOptions().stream()
                .anyMatch(o -> o.getText().contains(movieTitle)));
        selectByVisibleText(movieSelect, movieTitle);

        wait.until(d -> new Select(d.findElement(clientSelect)).getOptions().stream()
                .anyMatch(o -> o.getText().contains(clientOrganizationName)));
        selectByVisibleText(clientSelect, clientOrganizationName);

        setFieldValue(startDateInput, startDate);
        setFieldValue(endDateInput, endDate);
        setFieldValue(termsInput, terms);

        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(movieSelect));
    }

    public boolean isLicenseMovieVisible(String movieTitle) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")));
        return !driver.findElements(By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")).isEmpty();
    }

    private void setFieldValue(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
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
                field,
                value);
    }

    private void selectByVisibleText(By locator, String visibleText) {
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
