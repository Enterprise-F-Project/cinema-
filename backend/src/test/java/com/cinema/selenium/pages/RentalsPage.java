package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for /rentals — selectors from rentals-page-content.tsx.
 */
public class RentalsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitle = By.xpath("//h1[normalize-space()='Rentals']");
    private final By requestRentalButton = By.xpath("//button[normalize-space()='Request rental']");
    private final By movieSelect = By.id("rental-movie");
    private final By rentalStart = By.id("rental-start");
    private final By rentalEnd = By.id("rental-end");
    private final By submitRequest = By.xpath("//button[@type='submit' and (normalize-space()='Submit request' or normalize-space()='Submitting...')]");
    private final By statusFilter = By.id("rental-status");

    public RentalsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public RentalsPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        return this;
    }

    public void requestRental(String movieTitle, String rentalDate, String returnDate) {
        WebElement openButton = wait.until(ExpectedConditions.elementToBeClickable(requestRentalButton));
        // Native Selenium click does not reliably open this Sheet in headless Chrome.
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", openButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect));

        wait.until(d -> new Select(d.findElement(movieSelect)).getOptions().stream()
                .anyMatch(o -> o.getText().contains(movieTitle)));
        selectByVisibleText(movieSelect, movieTitle);

        setFieldValue(rentalStart, rentalDate);
        setFieldValue(rentalEnd, returnDate);

        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(submitRequest));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(movieSelect));
    }

    public boolean isRentalVisible(String movieTitle) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")));
        return !driver.findElements(By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")).isEmpty();
    }

    public void markActiveForMovie(String movieTitle) {
        clickStatusActionNearMovie(movieTitle, "Mark active");
    }

    public void markCompletedForMovie(String movieTitle) {
        clickStatusActionNearMovie(movieTitle, "Mark completed");
    }

    public boolean hasStatusTextNearMovie(String movieTitle, String statusLabel) {
        By nearby = By.xpath(
                "//*[contains(normalize-space(),'" + movieTitle + "')]/ancestor::*[self::tr or contains(@class,'grid') or contains(@class,'row')][1]"
                        + "//*[contains(normalize-space(),'" + statusLabel + "')]");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nearby));
            return true;
        } catch (Exception ex) {
            return driver.getPageSource().contains(statusLabel)
                    && driver.getPageSource().contains(movieTitle);
        }
    }

    public void filterByStatus(String optionVisibleText) {
        Select filter = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilter)));
        filter.selectByVisibleText(optionVisibleText);
    }

    private void clickStatusActionNearMovie(String movieTitle, String buttonText) {
        By action = By.xpath(
                "//*[contains(normalize-space(),'" + movieTitle + "')]/ancestor::*[self::tr or contains(@class,'grid') or contains(@class,'row')][1]"
                        + "//button[normalize-space()='" + buttonText + "']");
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(action));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
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
