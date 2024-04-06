package race.download;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import race.download.model.review.*;
import race.download.model.sectionals.*;

public class SectWebLoader {
	private ChromeDriver driver;
	final private Wait<WebDriver> wait;

	public SectWebLoader(ChromeDriver driver, Wait<WebDriver> wait) {
		this.driver = driver;
		this.wait = wait;
	}

	public SectMeeting process(String url) {
		SectMeeting meeting = new SectMeeting();
		driver.get(url);
		WebElement content = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector("div#dvRacingSectionals"));
			}
		});
		List<WebElement> raceElements = content.findElements(By.cssSelector("div.racing-meet-race-panel"));
		ArrayList<SectRace> races = new ArrayList<SectRace>();
		for (WebElement raceEl : raceElements) {
			if (hasSectionals(raceEl)) {
				races.add(getRace(raceEl));
			}
		}
		meeting.setRaces(races);
		return meeting;
	}

	private SectRace getRace(WebElement raceEl) {
		SectRace race = new SectRace();
		ArrayList<SectHorse> horses = new ArrayList<SectHorse>();
		race.setDistance(Integer.valueOf(raceEl.findElement(By.cssSelector("div.race-time span:nth-child(2)"))
				.getAttribute("innerHTML").replaceFirst("m", "")));
		race.setNumber(Integer.valueOf(raceEl.getAttribute("data-race-number")));
		List<WebElement> horseEls = raceEl
				.findElements(By.cssSelector("div.sectional-horse tbody tr div.sect-placing-name"));
		List<WebElement> sectEls = raceEl.findElements(By.cssSelector("div.sectional-wrap tbody tr"));
		for(int i = 0;i < horseEls.size();i++) {
			horses.add(getHorse(horseEls.get(i), sectEls.get(i)));
		}
		race.setHorses(horses);	
		return race;
	}
	
	private SectHorse getHorse(WebElement horseEl, WebElement sectEl) {
		SectHorse horse = new SectHorse();
		ArrayList<Sectional> sects = new ArrayList<Sectional>();
		String name = horseEl.getAttribute("innerHTML").split("\\.|\\(")[1].trim().toUpperCase().replaceAll("\\N{Right Single Quotation Mark}", "'");
		horse.setName(name);
		List<WebElement> sectEls = sectEl.findElements(By.cssSelector("div.sect-time-rank:nth-child(1)"));
		for(WebElement sel : sectEls) {
			Sectional sect = new Sectional();
			String [] split = sel.getAttribute("innerHTML").split("\\[|\\]");
			sect.setSect(getTime(split[0]));
			sect.setPosition(Integer.valueOf(split[1]));
			sect.setSectType(1);
			sect.setSplitType(1);
			sect.setIsHorse(true);
			sects.add(sect);
		}
		horse.setSectionals(sects);
		horse.setActualSettle(sects.get(1).getPosition());
		return horse;
	}

	private Boolean hasSectionals(WebElement el) {
		List<WebElement> sectionals = el.findElements(By.cssSelector("div.sectional-times"));
		if (sectionals.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private Double getTime(String sect) {
		String[] split = sect.split(":");
		return (Double.valueOf(split[0]) * 60.0) + Double.valueOf(split[1]);
	}

}
