package com.cinema.selenium;

import com.cinema.selenium.pages.DashboardPage;
import com.cinema.selenium.pages.LicensesPage;
import com.cinema.selenium.pages.LoginPage;
import com.cinema.selenium.pages.MoviesPage;
import com.cinema.selenium.pages.RentalsPage;
import com.cinema.selenium.support.BaseSeleniumTest;
import com.cinema.selenium.support.SeleniumConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * System / UI end-to-end tests for The Cinema Distribution System.
 * Requires frontend (localhost:3000), backend (8080), and PostgreSQL with V7 demo users.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CinemaSystemTest extends BaseSeleniumTest {

    private static final String DEMO_CLIENT_ORG = "Demo Cinema";

    @Test
    @Order(1)
    @DisplayName("CLIENT can login and navigate Movies and Rentals")
    void clientLogin_navigatesToMoviesAndRentals() {
        open("/login");
        new LoginPage(driver, wait).loginAs(SeleniumConfig.clientEmail(), SeleniumConfig.demoPassword());

        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();
        assertThat(dashboard.signedInText()).containsIgnoringCase("Client");

        dashboard.goToMovies();
        new MoviesPage(driver, wait).waitUntilLoaded();
        assertThat(driver.getCurrentUrl()).contains("/movies");

        dashboard.goToRentals();
        new RentalsPage(driver, wait).waitUntilLoaded();
        assertThat(driver.getCurrentUrl()).contains("/rentals");
        assertThat(driver.findElements(By.xpath("//button[normalize-space()='Request rental']"))).isNotEmpty();

        dashboard.logout();
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @Order(2)
    @DisplayName("DISTRIBUTOR can create a movie and see it in the catalog")
    void distributor_createsMovie_visibleInCatalog() {
        String title = "Selenium Catalog " + System.currentTimeMillis();

        open("/login");
        new LoginPage(driver, wait).loginAs(SeleniumConfig.distributorEmail(), SeleniumConfig.demoPassword());

        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();
        assertThat(dashboard.signedInText()).containsIgnoringCase("Distributor");

        dashboard.goToMovies();
        MoviesPage moviesPage = new MoviesPage(driver, wait).waitUntilLoaded();
        moviesPage.createMovie(title, "Drama", "2024-05-01", "120", "Created by Selenium system test");

        moviesPage.filterByTitle(title);
        wait.until(d -> moviesPage.isMovieVisibleInTable(title));
        assertThat(moviesPage.isMovieVisibleInTable(title)).isTrue();

        dashboard.logout();
    }

    @Test
    @Order(3)
    @DisplayName("Rental lifecycle: license movie, request rental, activate, complete")
    void rentalLifecycle_license_request_activate_complete() {
        String movieTitle = "Selenium Rental " + System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        String start = today.toString();
        String end = today.plusMonths(6).toString();
        String rentalStart = today.toString();
        String rentalEnd = today.plusDays(7).toString();

        // --- Distributor: create AVAILABLE movie ---
        open("/login");
        new LoginPage(driver, wait).loginAs(SeleniumConfig.distributorEmail(), SeleniumConfig.demoPassword());
        DashboardPage distributorDash = new DashboardPage(driver, wait).waitUntilLoaded();
        distributorDash.goToMovies();
        new MoviesPage(driver, wait).waitUntilLoaded()
                .createMovie(movieTitle, "Action", "2024-08-15", "110", "Rental lifecycle movie");

        // --- Distributor: create ACTIVE license for Demo Cinema ---
        distributorDash.goToLicenses();
        LicensesPage licensesPage = new LicensesPage(driver, wait).waitUntilLoaded();
        licensesPage.createLicenseForMovieAndClient(
                movieTitle, DEMO_CLIENT_ORG, start, end, "Selenium test license");
        assertThat(licensesPage.isLicenseMovieVisible(movieTitle)).isTrue();
        distributorDash.logout();

        // --- Client: request rental and progress status ---
        open("/login");
        new LoginPage(driver, wait).loginAs(SeleniumConfig.clientEmail(), SeleniumConfig.demoPassword());
        DashboardPage clientDash = new DashboardPage(driver, wait).waitUntilLoaded();
        clientDash.goToRentals();

        RentalsPage rentalsPage = new RentalsPage(driver, wait).waitUntilLoaded();
        rentalsPage.requestRental(movieTitle, rentalStart, rentalEnd);
        assertThat(rentalsPage.isRentalVisible(movieTitle)).isTrue();
        assertThat(rentalsPage.hasStatusTextNearMovie(movieTitle, "Requested")).isTrue();

        rentalsPage.markActiveForMovie(movieTitle);
        wait.until(d -> rentalsPage.hasStatusTextNearMovie(movieTitle, "Active"));
        assertThat(rentalsPage.hasStatusTextNearMovie(movieTitle, "Active")).isTrue();

        rentalsPage.markCompletedForMovie(movieTitle);
        wait.until(d -> rentalsPage.hasStatusTextNearMovie(movieTitle, "Completed"));
        assertThat(rentalsPage.hasStatusTextNearMovie(movieTitle, "Completed")).isTrue();

        clientDash.logout();
        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
