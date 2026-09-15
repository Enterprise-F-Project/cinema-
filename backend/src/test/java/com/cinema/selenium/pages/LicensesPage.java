package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        wait.until(ExpectedConditions.elementToBeClickable(createLicenseButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect));

        Select movies = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(movieSelect)));
        wait.until(d -> movies.getOptions().stream().anyMatch(o -> o.getText().contains(movieTitle)));
        movies.selectByVisibleText(movieTitle);

        Select clients = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(clientSelect)));
        wait.until(d -> clients.getOptions().stream().anyMatch(o -> o.getText().contains(clientOrganizationName)));
        clients.selectByVisibleText(clientOrganizationName);

        type(startDateInput, startDate);
        type(endDateInput, endDate);
        type(termsInput, terms);

        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(movieSelect));
    }

    public boolean isLicenseMovieVisible(String movieTitle) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")));
        return !driver.findElements(By.xpath("//*[contains(normalize-space(),'" + movieTitle + "')]")).isEmpty();
    }

    private void type(By locator, String value) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
    }
}
