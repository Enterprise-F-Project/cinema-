package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        wait.until(ExpectedConditions.elementToBeClickable(addMovieButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(movieTitleInput));
    }

    public void createMovie(String title, String genre, String releaseDate, String durationMinutes, String description) {
        openAddMovieSheet();
        type(movieTitleInput, title);
        type(movieGenreInput, genre);
        type(movieReleaseInput, releaseDate);
        type(movieDurationInput, durationMinutes);
        type(movieDescriptionInput, description);
        wait.until(ExpectedConditions.elementToBeClickable(createMovieSubmit)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(movieTitleInput));
    }

    public void filterByTitle(String title) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(titleFilter));
        field.clear();
        field.sendKeys(title);
    }

    public boolean isMovieVisibleInTable(String title) {
        By rowText = By.xpath("//table//td[contains(.,'" + title + "')] | //*[contains(@class,'table') or self::table]//*[contains(normalize-space(),'" + title + "')]");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'" + title + "')]")));
            return driver.findElements(By.xpath("//*[contains(normalize-space(),'" + title + "')]")).stream()
                    .anyMatch(el -> el.isDisplayed() && el.getText().contains(title));
        } catch (Exception ex) {
            return false;
        }
    }

    private void type(By locator, String value) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
    }
}
