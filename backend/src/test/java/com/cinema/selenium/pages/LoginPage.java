package com.cinema.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for /login — selectors from login-form.tsx (#email, #password, Sign in).
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By signInButton = By.xpath("//button[@type='submit' and (normalize-space()='Sign in' or normalize-space()='Signing in...')]");
    private final By welcomeHeading = By.xpath("//h1[normalize-space()='Welcome back']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeHeading));
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        return this;
    }

    public LoginPage enterEmail(String email) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public void submit() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
    }

    public void loginAs(String email, String password) {
        waitUntilLoaded();
        enterEmail(email);
        enterPassword(password);
        submit();
    }
}
