package race.download;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.JavascriptExecutor;
import race.download.model.track.Track;
import race.download.model.review.*;
import race.download.model.speedmap.SmMeeting;

public class ReviewProcessor {
	private ChromeDriver driver;
	private Wait<WebDriver> wait;

	public ReviewProcessor() {
		setupDriver();
		wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(StaleElementReferenceException.class, NoSuchElementException.class);
	}

	public RvMeeting process(Track track, LocalDate date, HashMap<String, String> urls, SmMeeting speedmap,
			Boolean isSectionals, Boolean isWeb) {

		ResultsProcessor results = new ResultsProcessor(driver, wait);
		RvMeeting meeting = results.process(track, date, urls.get("results"));
		HashMap<String, HorseIndex> indices = getIndices(meeting);
		MapProcessor mapProcessor = new MapProcessor();
		Boolean mapOk = mapProcessor.process(meeting, speedmap, indices);
		if (!mapOk) {
			driver.quit();
			System.exit(1);
		}
		PreStewardsProcessor preStewardsProcessor = new PreStewardsProcessor(driver, wait);
		preStewardsProcessor.process(meeting, urls.get("preStewards"), indices);
		GearProcessor gearProcessor = new GearProcessor(driver, wait);
		gearProcessor.process(meeting, urls.get("gear"), indices);
		PriceProcessor priceProcessor = new PriceProcessor();
		priceProcessor.process(meeting, indices);
		SectProcessor sectProcessor = new SectProcessor(driver, wait);
		sectProcessor.process(meeting, urls.get("sectionals"), isWeb, indices);
		PostStewardsProcessor postStewardsProcessor = new PostStewardsProcessor();
		postStewardsProcessor.process(meeting, indices);
		System.out.println(meeting);
		driver.quit();
		return meeting;
	}

	private HashMap<String, HorseIndex> getIndices(RvMeeting meeting) {
		HashMap<String, HorseIndex> horseIndices = new HashMap<>();
		List<Race> races = meeting.getRaces();
		int numRaces = races.size();
		for (int i = 0; i < numRaces; i++) {
			Race race = races.get(i);
			List<Horse> horses = race.getHorses();
			int numHorses = horses.size();
			for (int j = 0; j < numHorses; j++) {
				Horse horse = horses.get(j);
				horseIndices.put(horse.getName().toUpperCase(), new HorseIndex(i, j));
			}
		}
		return horseIndices;
	}

	private void setupDriver() {

		System.setProperty("webdriver.chrome.driver",
				"C:/Users/dappa/chromedriver/chromedriver-win64/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--remote-allow-origins=*");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
	}

}
