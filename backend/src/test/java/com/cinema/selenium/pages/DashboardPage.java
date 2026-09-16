package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Authenticated shell helpers: sidebar nav + account logout.
 * Nav titles from navigation.ts; logout from app-header.tsx.
 */
public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By signedInAs = By.xpath("//p[contains(.,'Signed in as')]");
    private final By accountMenuButton = By.cssSelector("button[aria-label='Open account menu']");
    private final By logOutItem = By.xpath("//*[self::div or self::button or self::span][normalize-space()='Log out']");

    public DashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public DashboardPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(signedInAs));
        return this;
    }

    public String signedInText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(signedInAs)).getText();
    }

    public void openNav(String navTitle) {
        By link = By.xpath("//aside//nav//a[normalize-space()='" + navTitle + "']");
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(link));
        // Native center-click does not reliably activate Next.js <Link> in this sidebar.
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void goToMovies() {
        openNav("Movies");
        wait.until(ExpectedConditions.urlContains("/movies"));
    }

    public void goToLicenses() {
        openNav("Licenses");
        wait.until(ExpectedConditions.urlContains("/licenses"));
    }

    public void goToRentals() {
        openNav("Rentals");
        wait.until(ExpectedConditions.urlContains("/rentals"));
    }

    public void logout() {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(accountMenuButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logOutItem));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);
        wait.until(ExpectedConditions.urlContains("/login"));
    }
}
