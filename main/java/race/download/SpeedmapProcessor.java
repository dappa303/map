package race.download;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import race.download.model.speedmap.SmHorse;
import race.download.model.speedmap.SmRace;
import race.download.model.speedmap.SmMeeting;

public class SpeedmapProcessor {

	private ChromeDriver driver;
	private Wait<WebDriver> wait;
	private HashMap silks;

	public SpeedmapProcessor() {

		setupDriver();
		wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(StaleElementReferenceException.class, NoSuchElementException.class);

	}

	public SmMeeting process(String url, String track, LocalDate date) {

		driver.get(url);

		WebElement content = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector("div#info-container"));
			}
		});


		loadSilks(content);

		String details = content.findElement(By.cssSelector("div.race-venue-bottom > div.col1")).getText();
		String rail = details.split("Position:", 2)[1].split("Dual", 2)[0].trim();
		String raceClass = content.findElement(By.cssSelector("tr.race-info > td")).getText();
		raceClass = raceClass.split("Jockey\\s+Welfare\\s+Fund\\s+\\$[0-9,]{2,7}\\.")[1].trim();
		SmMeeting meeting = new SmMeeting();
		meeting.setTrack(track);
		meeting.setDate(date);
		meeting.setRail(rail);
		meeting.setRaces(new ArrayList<SmRace>());

		List<WebElement> raceTitles = content.findElements(By.cssSelector("table.race-title > tbody"));
		List<WebElement> raceFields = content.findElements(By.cssSelector("table.race-strip-fields > tbody"));

		for (int i = 0; i < raceTitles.size(); i++) {
			meeting.getRaces().add(getRace(raceTitles.get(i), raceFields.get(i)));
		}

		System.out.println(meeting);

		driver.quit();

		return meeting;
	}

	private SmRace getRace(WebElement title, WebElement field) {
		String details = title.findElement(By.cssSelector("a.race-title-anchor")).getText().trim();
		Integer number = Integer.valueOf(details.split("\\s+", 3)[1]);
		String raceClass = title.findElement(By.cssSelector("tr.race-info > td")).getText();
		raceClass = raceClass.split("Jockey\\s+Welfare\\s+Fund\\s+\\$[0-9,]{2,7}\\.")[1].trim();
		if (raceClass.contains("BOBS")) {
			raceClass = raceClass.split("BOBS")[0].trim();
		} else {
			raceClass = raceClass.split("Track\\s+Name:")[0].trim();
		}
		raceClass = raceClass.replaceAll("[\\r\\n]+", ", ");
		SmRace race = new SmRace();
		race.setDetails(details);
		race.setRaceClass(raceClass);
		race.setNumber(number);
		race.setPace("Solid");
		race.setCondition("Good 4");
		race.setHorses(new ArrayList<SmHorse>());
		List<WebElement> runners = field.findElements(By.cssSelector("tr"));
		for (WebElement runner : runners) {
			SmHorse horse = getHorse(runner);
			if (horse != null)
				race.getHorses().add(horse);
		}
		long numStarters = race.getHorses().stream().filter(h -> h.getIsScratched() == false).count();
		int runnerIndex = Integer.valueOf((int) numStarters - 1);
		int scratchingIndex = 0;
		for (SmHorse h : race.getHorses()) {
			if (!h.getIsScratched()) {
				h.setPositionIndex(runnerIndex);
				runnerIndex--;
			} else {
				h.setPositionIndex(scratchingIndex);
				scratchingIndex++;
			}
		}
		return race;
	}

	private SmHorse getHorse(WebElement runner) {
		SmHorse horse = new SmHorse();
		Boolean isScratched = false;
		String fullClass = runner.getAttribute("class");
		if (fullClass.contains("Scratched")) {
			isScratched = true;
		}
		List<WebElement> parts = runner.findElements(By.cssSelector("td"));
		String numberString = parts.get(0).getText().trim();
		numberString = numberString.replaceFirst("e", "");
		Integer number = Integer.valueOf(numberString);
		String nationality = null;
		String name = parts.get(2).getText().trim();
		if (name.contains("(")) {
			String[] nameParts = name.split("\\(", 2);
			name = nameParts[0].trim();
			nationality = nameParts[1].replaceFirst("\\)", "").trim();
		}
		name = name.replaceAll("\\N{Right Single Quotation Mark}", "'").toUpperCase();
		String img = (String)silks.get(name);
		name = Arrays.stream(name.split("\\s+"))
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
		String trainer = parts.get(3).getText().trim();
		String jockey = parts.get(4).getText().trim();
		if (jockey.contains("(")) {
			jockey = jockey.split("\\(", 2)[0].trim();
		}
		Integer barrier = Integer.valueOf(parts.get(5).getText().trim());
		// occasionally scratched horses are given barrier 0 on past races
		if (barrier == 0)
			return null;
		String weight = parts.get(6).getText().trim();
		String adjustedWeight = parts.get(7).getText().trim();
		if (adjustedWeight.equals(""))
			adjustedWeight = null;

		horse.setNumber(number);
		horse.setName(name);
		horse.setNationality(nationality);
		horse.setTrainer(trainer);
		horse.setJockey(jockey);
		horse.setBarrier(barrier);
		horse.setWeight(weight);
		horse.setAdjustedWeight(adjustedWeight);
		horse.setSilks(img);
		horse.setIsScratched(isScratched);
		return horse;
	}
	
	private void loadSilks(WebElement content){
		silks = new HashMap<String,String>();
		List<WebElement> silksElements = driver.findElements(By.cssSelector("table.horse-form-table"));
		for (WebElement silksEl : silksElements) {
			String name = silksEl.findElement(By.cssSelector("span.horse-name > a")).getText().trim();
			if (name.contains("(")) {
				String[] splitName = name.split("\\(");
				name = splitName[0].trim();
			}
			name = name.replaceAll("�", "'");
			String imgPath = silksEl.findElement(By.cssSelector("div.Silks > img")).getAttribute("src");
			String[] splitPath = imgPath.split("/");
			String img = splitPath[splitPath.length - 1].trim();
			silks.put(name.toUpperCase(), img);
		}
	}

	

	private void setupDriver() {

		System.setProperty("webdriver.chrome.driver", "C:\\Users\\dappa\\chromedriver\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("--remote-allow-origins=*");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
	}
}
