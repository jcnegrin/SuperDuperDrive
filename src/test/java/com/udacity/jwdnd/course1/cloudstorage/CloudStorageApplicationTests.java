package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageApplicationTests {

	private static String firstName = "Juan";
	private static String lastName = "Negrin";
	private static String userName = "root";
	private static String password = "password";
	private static String noteTitle = "Doing tests";
	private static String noteDescription = "Test good description";
	private static String credURL = "jcnegrin.com";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	@Order(1)
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(2)
	public void getSignupPage() {
		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	@Order(3)
	public void getUnauthorizedPage() {
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(4)
	public void registerNewUser() {

		// First, let us register an user

		driver.get("http://localhost:" + this.port + "/signup");
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.sendKeys(firstName);
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.sendKeys(lastName);
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.sendKeys(userName);
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.sendKeys(password);
		WebElement signUpButton = driver.findElement(By.id("signupBtn"));
		signUpButton.click();

		// Trying to login with user credentials created before
		driver.get("http://localhost:" + this.port + "/login");
		inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.sendKeys(userName);
		inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.sendKeys(password);
		WebElement loginButton = driver.findElement(By.id("loginBtn"));
		loginButton.click();
		Assertions.assertEquals("Home", driver.getTitle());
	}

	@Test
	@Order(5)
	public void createNoteTest() {

		WebDriverWait wait = new WebDriverWait (driver, 30);

		driver.get("http://localhost:" + this.port + "/login");
		WebElement inputUser = driver.findElement(By.id("inputUsername"));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputUser.sendKeys(userName);
		inputPassword.sendKeys(password);
		WebElement loginButton = driver.findElement(By.id("loginBtn"));
		loginButton.click();

		// Going to Notes Tab
		wait.withTimeout(Duration.ofSeconds(30));

		driver.get("http://localhost:" + this.port + "/home");
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
		wait.until(ExpectedConditions.elementToBeClickable(notesTab)).click();

		// Add note

		WebElement addNoteBtn = driver.findElement(By.id("addNoteBtn"));
		wait.until(ExpectedConditions.elementToBeClickable(addNoteBtn)).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title"))).sendKeys(noteTitle);
		WebElement noteDescriptionInput = driver.findElement(By.id("note-description"));
		noteDescriptionInput.sendKeys(noteDescription);

		WebElement noteSubmitBtn = driver.findElement(By.id("noteSubmitBtn"));
		noteSubmitBtn.click();

		Assertions.assertEquals("Result", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/home");
		notesTab = driver.findElement(By.id("nav-notes-tab"));
		notesTab.click();

		WebElement notesTable = driver.findElement(By.id("userTable"));
		List<WebElement> notesList = notesTable.findElements(By.tagName("th"));
		Boolean created = false;

		for (int i = 0; i < notesList.size(); i++) {
			WebElement element = notesList.get(i);
			if (element.getAttribute("innerHTML").equals(noteTitle)) {
				created = true;
				break;
			}
		}

		Assertions.assertTrue(created);

	}

	@Test
	@Order(6)
	public void updateNoteTest() {

		WebDriverWait wait = new WebDriverWait(driver, 30);

		driver.get("http://localhost:" + this.port + "/login");
		WebElement inputUser = driver.findElement(By.id("inputUsername"));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputUser.sendKeys(userName);
		inputPassword.sendKeys(password);
		WebElement loginButton = driver.findElement(By.id("loginBtn"));
		loginButton.click();

		// Updating a note

		driver.get("http://localhost:" + this.port + "/home");
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
		wait.until(ExpectedConditions.elementToBeClickable(notesTab)).click();

		WebElement editNoteBtn = driver.findElement(By.id("editNoteBtn"));
		wait.until(ExpectedConditions.elementToBeClickable(editNoteBtn)).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title"))).sendKeys(" " + "updated");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("note-description"))).sendKeys(" " + "updated");

		WebElement noteSubmitBtn = driver.findElement(By.id("noteSubmitBtn"));
		noteSubmitBtn.click();

		Assertions.assertEquals("Result", driver.getTitle());

		// Checking if note was updated

		driver.get("http://localhost:" + this.port + "/home");
		notesTab = driver.findElement(By.id("nav-notes-tab"));
		wait.until(ExpectedConditions.elementToBeClickable(notesTab)).click();

		WebElement notesTable = driver.findElement(By.id("userTable"));
		List<WebElement> notesList = notesTable.findElements(By.tagName("th"));
		Boolean updated = false;

		for (int i = 0; i < notesList.size(); i++) {
			WebElement element = notesList.get(i);
			if (element.getAttribute("innerHTML").equals(noteTitle + " updated")) {
				updated = true;
				break;
			}
		}

		Assertions.assertTrue(updated);

	}

	@Test
	@Order(7)
	public void noteDeletionTest() {

		WebDriverWait wait = new WebDriverWait(driver, 30);

		// Login
		driver.get("http://localhost:" + this.port + "/login");
		WebElement inputUser = driver.findElement(By.id("inputUsername"));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputUser.sendKeys(userName);
		inputPassword.sendKeys(password);
		WebElement loginButton = driver.findElement(By.id("loginBtn"));
		loginButton.click();

		// Deleting a note

		driver.get("http://localhost:" + this.port + "/home");
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
		wait.until(ExpectedConditions.elementToBeClickable(notesTab)).click();

		WebElement notesTable = driver.findElement(By.id("userTable"));
		List<WebElement> notesList = notesTable.findElements(By.tagName("th"));
		int notesCounter = notesList.size();

		WebElement deleteNoteBtn = driver.findElement(By.id("deleteNoteBtn"));
		wait.until(ExpectedConditions.elementToBeClickable(deleteNoteBtn)).click();

		Assertions.assertEquals("Result", driver.getTitle());

		// Checking if note was deleted

		driver.get("http://localhost:" + this.port + "/home");
		notesTab = driver.findElement(By.id("nav-notes-tab"));
		notesTab.click();

		notesTable = driver.findElement(By.id("userTable"));
		notesList = notesTable.findElements(By.tagName("th"));

		Assertions.assertTrue(notesCounter > notesList.size());

	}

}
