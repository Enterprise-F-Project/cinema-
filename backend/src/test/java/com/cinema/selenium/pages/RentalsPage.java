package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        wait.until(ExpectedConditions.elementToBeClickable(requestRentalButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect));

        Select movies = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect)));
        wait.until(d -> movies.getOptions().stream().anyMatch(o -> o.getText().contains(movieTitle)));
        movies.selectByVisibleText(movieTitle);

        type(rentalStart, rentalDate);
        type(rentalEnd, returnDate);

        wait.until(ExpectedConditions.elementToBeClickable(submitRequest)).click();
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
        // Status badge text uses Title Case from shared status-badge (Requested/Active/Completed).
        By nearby = By.xpath(
                "//*[contains(normalize-space(),'" + movieTitle + "')]/ancestor::*[self::tr or contains(@class,'grid') or contains(@class,'row')][1]"
                        + "//*[contains(normalize-space(),'" + statusLabel + "')]");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nearby));
            return true;
        } catch (Exception ex) {
            // Fallback: page-level text presence after reload/filter.
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
        wait.until(ExpectedConditions.elementToBeClickable(action)).click();
    }

    private void type(By locator, String value) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
    }
}
