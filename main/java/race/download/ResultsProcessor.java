package race.download;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
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

public class ResultsProcessor {
	
	private ChromeDriver driver;
	private Wait<WebDriver> wait;
	
	public ResultsProcessor(ChromeDriver driver, Wait<WebDriver> wait) {
		this.driver = driver;
		this.wait = wait;
	}
	
	public RvMeeting process(Track track, LocalDate date, String url) {
		
		RvMeeting meeting = new RvMeeting();
		driver.get(url);
		WebElement content = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector("div#main-content"));
			}
		});
		String meetingDetails = content.findElement(By.cssSelector("div.race-venue-bottom > div.col1")).getText();
		String[] splitDetails = meetingDetails.split("\\R");
		String rail = splitDetails[0].split(":\\s*")[1].trim();
		Integer condition = Integer.valueOf(splitDetails[3].split("\\s+")[3]);
		String weather = splitDetails[4].split(":\\s*")[1];
		Double penotrometer = Double.valueOf(splitDetails[5].split(":\\s*")[1]);
		String[] splits = splitDetails[6].split("\\s+|,\\s*");
		Double goingStick = Double.valueOf(splits[4]);
		Double shear = Double.valueOf(splits[6]);
		meeting.setTrack(track.getName());
		meeting.setDate(date);
		meeting.setRail(rail);
		meeting.setCondition(condition);
		meeting.setWeather(weather);
		meeting.setPenotrometer(penotrometer);
		meeting.setGoingStick(goingStick);
		meeting.setShear(shear);
		List<WebElement> raceElements = content.findElements(By.cssSelector("table.race-title > tbody"));
		List<WebElement> fieldElements = content.findElements(By.cssSelector("table.race-strip-fields > tbody"));
		ArrayList<Race> races = new ArrayList<Race>();
		Integer numStarters = 0;
		for (int i = 0; i < raceElements.size(); i++) {
			Race race = getRace(raceElements.get(i), fieldElements.get(i),date, track.getVidCode());
			races.add(race);
			numStarters += race.getHorses().size();
		}
		meeting.setNumRunners(numStarters);
		meeting.setRaces(races);
		return meeting;
	}

	private Race getRace(WebElement raceEl, WebElement fieldEl, LocalDate date, String vidCode) {
		Race race = new Race();
		Integer number = Integer.valueOf(
				raceEl.findElement(By.cssSelector("tr:first-child a")).getAttribute("name").replaceFirst("Race", ""));
		String nameDist = raceEl.findElement(By.cssSelector("tr:first-child a")).getText();
		String[] splits = nameDist.split("\\d[AP]M\\s*|\\(|METRES\\)");
		String name = splits[1].trim();
		Integer distance = Integer.valueOf(splits[2].trim());
		splits = raceEl.findElement(By.cssSelector("tr:last-child")).getText().split("\\R");
		String[] splitPrize = splits[4].split("\\.");
		Integer prize = Integer.valueOf(splitPrize[0].replaceAll("Of\\s*\\$|,", "").trim());
		String raceClass = splits[5].trim();
		if (splitPrize.length == 3)
			raceClass = raceClass.replaceFirst("\\.", "") + ", " + splitPrize[2].trim() + ".";
		Boolean isBOBS = true;
		if (splits.length % 2 == 0)
			isBOBS = false;
		Integer ind = 7;
		if (isBOBS)
			ind++;
		String[] splitDetails = splits[ind].split(":\\s+");
		Integer condition = Integer.valueOf(splitDetails[3].split("\\s+")[1]);
		Double officialTime = getTime(splitDetails[4].split("\\s+")[0]);
		Double officialLast600 = getTime(splitDetails[5].split("\\s+")[0]);
		String comment = null;
		if(splits.length > 9) {
			ind = 9;
			if (isBOBS)
				ind++;
			comment = splits[ind].split(":\\s+")[1].trim();
		}
		String video = getVidName(date, vidCode, number, false);
		String stewardsVideo = getVidName(date, vidCode, number, true);
		List<WebElement> horseElements = fieldEl.findElements(By.cssSelector("tr[class$='Row']"));
		ArrayList<Horse> horses = new ArrayList<Horse>();
		for (WebElement horseEl : horseElements) 
			horses.add(getHorse(horseEl));
		race.setNumber(number);
		race.setName(name);
		race.setDistance(distance);
		race.setCondition(condition);
		race.setPrizeMoney(prize);
		race.setRaceClass(raceClass);
		race.setOfficialComment(comment);
		race.setOfficialTime(officialTime);
		race.setOfficialLast600(officialLast600);
		race.setVideo(video);
		race.setStewardsVideo(stewardsVideo);
		race.setVideoDifference(0.0);
		race.setHorses(horses);
		return race;
	}
	
	private Horse getHorse(WebElement el) {
		Horse horse = new Horse();
		String txt = el.findElement(By.cssSelector("td:nth-child(1) img")).getAttribute("src").trim();
		String [] splits = txt.split("/");
		String image = splits[splits.length - 1].trim();
		txt = el.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
		Integer pos = null;
		String dnf = null;
		if(txt.matches("\\d{1,2}")) {
			pos = Integer.valueOf(txt);
		}else {
			pos = 99;
			dnf = txt;
		}
		txt = el.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
		Integer number = Integer.valueOf(txt.replaceFirst("e", ""));
		txt = el.findElement(By.cssSelector("td:nth-child(4)")).getText().trim();
		splits = txt.split("\\s*\\(");
		String name = splits[0].replaceAll("\\N{Right Single Quotation Mark}","'"); 
		name = Arrays.stream(name.split("\\s+"))
				.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
		String nationality = null;
		if(splits.length > 1)
			nationality = splits[1].replaceFirst("\\)", "").trim();
		String trainer = el.findElement(By.cssSelector("td:nth-child(5)")).getText().trim();
		Boolean apprentice = false;
		String jockey = null;
		txt = el.findElement(By.cssSelector("td:nth-child(6)")).getText().trim();
		if(txt.contains("(")) {
			apprentice = true;
			splits = txt.split("\\s*\\(");
			jockey = splits[0];
		}else {
			jockey = txt;
		}
		Double margin = 0.0;
		txt = el.findElement(By.cssSelector("td:nth-child(7)")).getText().trim();
		if(!txt.equals(""))
			margin = Double.valueOf(txt);
		txt = el.findElement(By.cssSelector("td:nth-child(8)")).getText().trim();
		Integer barrier = Integer.valueOf(txt);
		Double weight = null;
		Double carried = null;
		txt = el.findElement(By.cssSelector("td:nth-child(9)")).getText().trim();
		if(txt.contains("(")) {
			splits = txt.split("\\s*\\(");
			weight = Double.valueOf(splits[0]);
			carried = Double.valueOf(splits[1].replaceAll("cd|\\)", ""));
		}else {
			weight = Double.valueOf(txt);
		}
		horse.setImage(image);
		horse.setNumber(number);
		horse.setBarrier(barrier);
		horse.setName(name);
		horse.setNationality(nationality);
		horse.setPosition(pos);
		horse.setDnf(dnf);
		horse.setTrainer(trainer);
		horse.setJockey(jockey);
		horse.setHasApprentice(apprentice);
		horse.setMargin(margin);
		horse.setWeight(weight);
		horse.setCarriedWeight(carried);
		return horse;
	}


	private Double getTime(String strTime) {
		String[] split = strTime.split(":");
		return Double.valueOf(split[0]) * 60.0 + Double.valueOf(split[1]);
	}
	
	private String getVidName(LocalDate date, String code, Integer number, Boolean isStewards) {
		DecimalFormat nf = new DecimalFormat("00");
		String vid = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + code + "R" + nf.format(number.longValue());
		if(isStewards)
			vid += "S";
		vid += "_V.mp4";
		return vid;
	}
}
