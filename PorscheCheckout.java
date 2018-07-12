package com.porsche;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class PorscheCheckout {

	WebDriver driver;
	int firstPrice;
	int dipslayedPrice;
	int deliveryPrice;
	int colorPrice;
	int whellPrice;
	int seatPrice;
	int carbonFiberPrice;
	int speed7;
	int brakes;

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://www.porsche.com/usa/modelstart/");
	}

	@AfterClass
	public void down() throws InterruptedException {
		Thread.sleep(5000);
		driver.quit();
	}

	@Test(priority = 1)
	public void selectCar() throws InterruptedException {
		WebElement select718 = driver.findElement(By.xpath("//span[.='718']"));
		select718.click();
		Thread.sleep(2000);
		boolean selected = driver.getCurrentUrl().contains("718");
		assertTrue(selected);
	}

	@Test(priority = 2)
	public void basePriceDisplayed() throws InterruptedException {

		String firstPriceS = driver.findElement(By.xpath("//div[.='From $ 56,900.00*']")).getText();
		firstPrice = fromStringToInt(firstPriceS) / 100;
		System.out.println("firstPrice: " + firstPrice);
		WebElement buildPrice = driver.findElement(By.xpath("//span[.='Build & Price'][1]"));
		String windowBeforeClick = driver.getWindowHandle();
		buildPrice.click();
		Set<String> winHadleAfterClick = driver.getWindowHandles();
		for (String handle : winHadleAfterClick) {
			if (!windowBeforeClick.equals(handle)) {
				driver.switchTo().window(handle);
				// System.out.println(driver.getCurrentUrl());
				break;
			}
		}
		String displayedPriceS = driver.findElement(By.xpath(
				// "//div[@class='groupItems']//div[@class='ccaPrice'][contains(text(),'$56,900')]"
				"/html[1]/body[1]/div[4]/div[3]/div[4]/section[2]/section[2]/div[1]/div[1]/div[2]")).getText();

		dipslayedPrice = fromStringToInt(displayedPriceS);
		// System.out.println(dipslayedPrice);
		Assert.assertEquals(firstPrice, dipslayedPrice);

		// driver.close();
		// driver.switchTo().window(windowBeforeClick);

	}

	@Test(priority = 3)
	public void initialEquipmentPrice() {
		int eqPrice1 = eqPrice();
		assertEquals(eqPrice1, 0);
	}

	@Test(priority = 4)
	public void firstTotalPrice() {

		assertEquals(totalPriceExpected(), totalPrice());

	}

	// @Ignore
	@Test(priority = 5)
	public void verifyColorPrice() {
		WebElement selectColor = driver.findElement(By.xpath("//span[@style='background-color: rgb(0, 120, 138);']"));
		selectColor.click();
		colorPrice = fromStringToInt(driver
				.findElement(By.xpath(
						"/html[1]/body[1]/div[4]/section[1]/section[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]"))
				.getText());
		

		assertEquals(colorPrice, eqPrice());

		assertEquals(totalPriceExpected(), totalPrice());

	}

	// @Ignore
	@Test(priority = 6)
	public void addWhells() {
		driver.findElement(By.xpath("//div[@class='flyout-label-value']")).click();
		driver.findElement(By.xpath("//a[@class='subitem-entry'][contains(text(),'Wheels')]")).click();
		WebElement selectWhell = driver
				.findElement(By.xpath("//li[@id='s_exterieur_x_M433']//span[@class='img-element']"));
		selectWhell.click();
		whellPrice = fromStringToInt(driver
				.findElement(By.xpath(
						"/html[1]/body[1]/div[4]/section[1]/section[1]/div[2]/div[2]/div[2]/div[1]/div[1]/div[2]"))
				.getText());
		int eqsum1 = whellPrice + colorPrice;
		assertEquals(eqPrice(), eqsum1);

		assertEquals(totalPrice(), totalPriceExpected());

	}

	// 15.Select seats ‘Power Sport Seats (14-way) with Memory Package’
	// 16.Verify that Price for Equipment is the sum of Miami Blue price + 20"
	// Carrera Sport Wheels + Power Sport Seats (14-way) with Memory Package"
	// 17.Verify that total price is the sum of base price + Price for Equipment +
	// Delivery, Processing and Handling Fee
	@Test(priority = 7)
	public void addseats() throws InterruptedException {
		driver.findElement(By.xpath("//div[contains(text(),'Overview')]")).click();
		driver.findElement(By.xpath("//span[contains(text(),'Interior Colors & Seats')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@class='subitem-entry'][contains(text(),'Seats')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//div[@id='s_interieur_x_73_x_PP06_x_shorttext']")).click();
		seatPrice = fromStringToInt(driver.findElement(By.xpath(
				"/html[1]/body[1]/div[4]/section[1]/section[2]/div[2]/div[2]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[3]/div[1]"))
				.getText());
		int eqSum2 = whellPrice + colorPrice + seatPrice;
		assertEquals(eqSum2, eqPrice());
		assertEquals(totalPrice(), totalPriceExpected());
	}

	// 18.Click on Interior Carbon Fiber
	// 19.Select Interior Trim in Carbon Fiber i.c.w. Standard Interior
	// 20.Verify that Price for Equipment is the sum of Miami Blue price +
	// 20" Carrera Sport Wheels + Power Sport Seats (14-way) with Memory Package +
	// Interior Trim in Carbon Fiber i.c.w. Standard Interior"
	// 21.Verify that total price is the sum of base price + Price for Equipment +
	// Delivery, Processing and Handling Fee
	@Test(priority = 8)
	public void addCarbonFiber() throws InterruptedException {
		driver.findElement(By.xpath("//div[contains(text(),'Overview')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//span[contains(text(),'Options')]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(),'Interior Carbon Fiber')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//div[@id='vs_table_IIC_x_PEKH_x_c04_PEKH_x_shorttext']")).click();
		Thread.sleep(1000);
		carbonFiberPrice = fromStringToInt(driver.findElement(By.xpath(
				"/html[1]/body[1]/div[4]/section[1]/section[3]/div[2]/div[8]/div[2]/section[1]/div[1]/div[1]/div[2]/div[1]"))
				.getText());
		int eqSum3 = whellPrice + colorPrice + seatPrice + carbonFiberPrice;

		assertEquals(eqSum3, eqPrice());
		assertEquals(totalPrice(), totalPriceExpected());

	}

	/*
	 * 22.Click on Performance 23.Select 7-speed Porsche Doppelkupplung (PDK)
	 * 24.Select Porsche Ceramic Composite Brakes (PCCB) 25.Verify that Price for
	 * Equipment is the sum of Miami Blue price + 20" Carrera Sport Wheels + Power
	 * Sport Seats (14-way) with Memory Package + Interior Trim in Carbon Fiber
	 * i.c.w. Standard Interior + 7-speed Porsche Doppelkupplung (PDK) + Porsche
	 * Ceramic Composite Brakes (PCCB) 26.Verify that total price is the sum of base
	 * price + Price for Equipment + Delivery, Processing and Handling Fee
	 */
	@Test(priority = 9)
	public void Performance() throws InterruptedException {
		driver.findElement(By.xpath("//div[contains(text(),'Overview')]")).click();
		Thread.sleep(2000);
		//System.out.println(driver.findElement(By.xpath("//a[contains(text(),'Performance')]")).isDisplayed());
		if(!driver.findElement(By.xpath("//a[contains(text(),'Performance')]")).isDisplayed()) {
			driver.findElement(By.xpath("//span[contains(text(),'Options')]")).click();
		};
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(),'Performance')]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//div[@id='vs_table_IMG_x_M250_x_c14_M250_x_shorttext']")).click();
		Thread.sleep(2000);
		speed7 = fromStringToInt(driver.findElement(By.xpath(
				"/html[1]/body[1]/div[4]/section[1]/section[3]/div[2]/div[3]/div[2]/section[1]/div[2]/div[1]/div[2]/div[1]"))
				.getText());
		
		driver.findElement(By.xpath("//div[@id='vs_table_IMG_x_M450_x_c94_M450_x_shorttext']")).click();
		Thread.sleep(1500);
		brakes = fromStringToInt(driver.findElement(By.xpath(
				"/html[1]/body[1]/div[4]/section[1]/section[3]/div[2]/div[3]/div[2]/section[1]/div[10]/div[1]/div[2]/div[1]"))
				.getText());
		int eqSum4 = whellPrice + colorPrice + seatPrice + carbonFiberPrice + speed7 + brakes;
		System.out.println("eqSum3:" + eqSum4);
		System.out.println("eqPrice:" + eqPrice());
		System.out.println("totalPrice:" + totalPrice());
		System.out.println("totalPriceExpected:" + totalPriceExpected());
		assertEquals(eqSum4, eqPrice());
		assertEquals(totalPrice(), totalPriceExpected());
		
		

	}
	@Ignore
	@Test(priority=10)
	public void denemeTotal() {
		List<WebElement> prices = driver.findElements(By.xpath("//div[@id='main']//div[@class='ccaPrice']"));
		for (int i = 0; i < prices.size(); i++) {
			System.out.println(prices.get(i).getText());
			
		}
	}

	// ==============================================================================================================//
	private int fromStringToInt(String k) {
		k = k.replaceAll("[^0-9]", "");  // from $  56,000.00 *   -> 56000
		int toReturn = Integer.parseInt(k);
		return toReturn;
	}

	// 2
	private int eqPrice() {
		int k = fromStringToInt(driver
				.findElement(
						By.xpath("/html[1]/body[1]/div[4]/div[3]/div[4]/section[2]/section[2]/div[1]/div[2]/div[2]"))
				.getText());
		return k;
	}

	// 3
	private int deliveryPrice() {
		int k = fromStringToInt(driver
				.findElement(
						By.xpath("/html[1]/body[1]/div[4]/div[3]/div[4]/section[2]/section[2]/div[1]/div[3]/div[2]"))
				.getText());
		return k;
	}

	// 4
	private int totalPrice() {
		int k = fromStringToInt(driver
				.findElement(
						By.xpath("/html[1]/body[1]/div[4]/div[3]/div[4]/section[2]/section[2]/div[1]/div[4]/div[2]"))
				.getText());
		return k;
	}

	private int totalPriceExpected() {
		int k = dipslayedPrice + eqPrice() + deliveryPrice();
		return k;
	}
}
