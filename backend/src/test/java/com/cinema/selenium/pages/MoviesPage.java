package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for /movies — selectors from movies-page-content.tsx.
 */
public class MoviesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitle = By.xpath("//h1[normalize-space()='Movies']");
    private final By addMovieButton = By.xpath("//button[normalize-space()='Add movie']");
    private final By titleFilter = By.id("title-filter");
    private final By movieTitleInput = By.id("movie-title");
    private final By movieGenreInput = By.id("movie-genre");
    private final By movieReleaseInput = By.id("movie-release");
    private final By movieDurationInput = By.id("movie-duration");
    private final By movieDescriptionInput = By.id("movie-description");
    private final By createMovieSubmit = By.xpath("//button[@type='submit' and normalize-space()='Create movie']");

    public MoviesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public MoviesPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        return this;
    }

    public void openAddMovieSheet() {
        WebElement openButton = wait.until(ExpectedConditions.elementToBeClickable(addMovieButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", openButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieTitleInput));
    }

    public void createMovie(String title, String genre, String releaseDate, String durationMinutes, String description) {
        openAddMovieSheet();
        setFieldValue(movieTitleInput, title);
        setFieldValue(movieGenreInput, genre);
        setFieldValue(movieReleaseInput, releaseDate);
        setFieldValue(movieDurationInput, durationMinutes);
        setFieldValue(movieDescriptionInput, description);
        wait.until(ExpectedConditions.elementToBeClickable(createMovieSubmit)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(movieTitleInput));
    }

    public void filterByTitle(String title) {
        setFieldValue(titleFilter, title);
    }

    public boolean isMovieVisibleInTable(String title) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'" + title + "')]")));
            return driver.findElements(By.xpath("//*[contains(normalize-space(),'" + title + "')]")).stream()
                    .anyMatch(el -> el.isDisplayed() && el.getText().contains(title));
        } catch (Exception ex) {
            return false;
        }
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
}
