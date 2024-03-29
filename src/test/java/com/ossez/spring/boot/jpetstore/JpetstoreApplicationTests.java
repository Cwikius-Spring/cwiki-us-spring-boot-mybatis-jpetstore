/*
 *    Copyright 2016-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.ossez.spring.boot.jpetstore;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit.ScreenShooter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Kazuki Shimizu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JpetstoreApplicationTests {

	@LocalServerPort
	private int port;

	@Rule
	public ScreenShooter screenShooter = ScreenShooter.failedTests();

	@Before
	public void setupSelenide() {
		browser = HTMLUNIT;
		timeout = TimeUnit.SECONDS.toMillis(10);
		baseUrl = String.format("http://localhost:%d", port);
		fastSetValue = true;
	}

	@After
	public void logout() {
		SelenideElement element = $(By.linkText("Sign Out"));
		if (element.exists()) {
			element.click();
		}
	}

	@Test
	public void testOrder() {

		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to sign in page & sign
		$(By.linkText("Sign In")).click();
		$(By.name("username")).val("j2ee");
		$(By.name("password")).val("j2ee");
		$(By.id("login")).click();
		$(By.id("WelcomeContent")).$(By.tagName("span")).shouldBe(text("ABC"));

		// Search items
		$(By.name("keywords")).val("fish");
		$(By.id("searchProducts")).click();
		$$(By.cssSelector("#Catalog table tr")).shouldHaveSize(3);

		// Select item
		$(By.linkText("Fresh Water fish from China")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Goldfish"));

		// Add a item to the cart
		$(By.linkText("Add to Cart")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Shopping Cart"));

		// Checkout cart items
		$(By.linkText("Proceed to Checkout")).click();
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Input card information & Confirm order information
		$(By.name("creditCard")).val("9999999999");
		$(By.name("expiryDate")).val("04/2020");
		$(By.name("continue")).click();
		$(By.id("confirmMessage")).shouldBe(text("Please confirm the information below and then press submit..."));

		// Submit order
		$(By.id("order")).click();
		$(By.cssSelector(".messages li")).shouldBe(text("Thank you, your order has been submitted."));
		String orderId = $(By.id("orderId")).text();

		// Show profile page
		$(By.linkText("My Account")).click();
		$(By.cssSelector("#Catalog h3")).shouldBe(text("User Information"));

		// Show orders
		$(By.linkText("My Orders")).click();
		$(By.cssSelector("#Content h2")).shouldBe(text("My Orders"));

		// Show order detail
		$(By.linkText(orderId)).click();
		assertThat($(By.id("orderId")).text()).isEqualTo(orderId);

		// Sign out
		$(By.linkText("Sign Out")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

	}

	@Test
	public void testUpdateProfile() {
		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to sign in page & sign
		$(By.linkText("Sign In")).click();
		$(By.name("username")).val("j2ee");
		$(By.name("password")).val("j2ee");
		$(By.id("login")).click();
		$(By.id("WelcomeContent")).$(By.tagName("span")).shouldBe(text("ABC"));

		// Show profile page
		$(By.linkText("My Account")).click();
		$(By.cssSelector("#Catalog h3")).shouldBe(text("User Information"));
		$$(By.cssSelector("#Catalog table td")).get(1).shouldBe(text("j2ee"));

		// Edit account
		$(By.id("save")).click();
		$(By.cssSelector("#Catalog h3")).shouldBe(text("User Information"));
		$$(By.cssSelector("#Catalog table td")).get(1).shouldBe(text("j2ee"));
	}

	@Test
	public void testRegistrationUser() {
		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to sign in page & sign
		$(By.linkText("Sign In")).click();
		$(By.cssSelector("#Catalog p")).shouldBe(text("Please enter your username and password."));

		// Move to use registration page
		$(By.linkText("Register Now!")).click();
		$(By.cssSelector("#Catalog h3")).shouldBe(text("User Information"));

		// Create a new user
		String userId = String.valueOf(System.currentTimeMillis());
		$(By.name("username")).val(userId);
		$(By.name("password")).val("password");
		$(By.name("repeatedPassword")).val("password");
		$(By.name("firstName")).val("Jon");
		$(By.name("lastName")).val("MyBatis");
		$(By.name("email")).val("jon.mybatis@test.com");
		$(By.name("phone")).val("09012345678");
		$(By.name("address1")).val("Address1");
		$(By.name("address2")).val("Address2");
		$(By.name("city")).val("Minato-Ku");
		$(By.name("state")).val("Tokyo");
		$(By.name("zip")).val("0001234");
		$(By.name("country")).val("Japan");
		$(By.name("languagePreference")).selectOption("Japanese");
		$(By.name("favouriteCategoryId")).selectOption("CATS");
		$(By.name("listOption")).setSelected(true);
		$(By.name("bannerOption")).setSelected(true);
		$(By.id("save")).click();
		$(By.cssSelector(".messages li")).shouldBe(text("Your account has been created. Please try login !!"));

		// Move to sign in page & sign
		$(By.name("username")).val(userId);
		$(By.name("password")).val("password");
		$(By.id("login")).click();
		$(By.id("WelcomeContent")).$(By.tagName("span")).shouldBe(text("Jon"));

	}

	@Test
	public void testSelectItems() {
		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to category
		$(By.cssSelector("#SidebarContent a")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Fish"));

		// Move to items
		$(By.linkText("FI-SW-01")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Angelfish"));

		// Move to item detail
		$(By.linkText("EST-1")).click();
		$$(By.cssSelector("#Catalog table tr td")).get(2).shouldBe(text("Large Angelfish"));

		// Back to items
		$(By.linkText("Return to FI-SW-01")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Angelfish"));

		// Back to category
		$(By.linkText("Return to FISH")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Fish"));

		// Back to the top page
		$(By.linkText("Return to Main Menu")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

	}

	@Test
	public void testViewCart() {

		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to cart
		$(By.name("img_cart")).click();
		$(By.cssSelector("#Catalog h2")).shouldBe(text("Shopping Cart"));

	}

	@Test
	public void testViewHelp() {

		// Open the home page
		open("/");
		assertThat(title()).isEqualTo("JPetStore Demo");

		String mainWindow = getWebDriver().getWindowHandle();

		// Move to the top page
		$(By.linkText("Enter the Store")).click();
		$(By.id("WelcomeContent")).shouldBe(text(""));

		// Move to help
		$(By.linkText("?")).click();

		Set<String> windows = new HashSet<>(getWebDriver().getWindowHandles());
		windows.remove(mainWindow);
		switchTo().window(windows.iterator().next());

		$(By.cssSelector("#Content h1")).shouldBe(text("JPetStore Demo"));

	}

}
