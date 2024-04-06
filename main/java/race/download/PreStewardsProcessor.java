package race.download;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import race.download.model.review.*;

public class PreStewardsProcessor {
	
	private ChromeDriver driver;
	final private Wait<WebDriver> wait;
	
	public PreStewardsProcessor(ChromeDriver driver, Wait<WebDriver> wait) {
		this.driver = driver;
		this.wait = wait;
	}
	
	public void process(RvMeeting meeting, String url, HashMap<String, HorseIndex> indices) {
		DateTimeFormatter inFormat = DateTimeFormatter.ofPattern("d/MM/yyyy");
		DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("d MMM yy");
		
		driver.get(url);
		WebElement content = wait.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector("div#main-content"));
			}
		});
		List<WebElement> horseEls = content.findElements(By.cssSelector("tr[class$='Row']"));
		for(WebElement horseEl : horseEls) {
			String name = horseEl.findElement(By.cssSelector("td.horse")).getText().trim().replaceAll("\\N{Right Single Quotation Mark}","'");
			if(name.contains("(")) {
				name = name.split("\\(")[0].trim();
			}
			String dateStr = horseEl.findElement(By.cssSelector("td.date")).getText().trim();
			LocalDate date = LocalDate.parse(dateStr, inFormat);
			dateStr = date.format(outFormat);
			String track = horseEl.findElement(By.cssSelector("td.venue")).getText().trim();
			if(track.equals("Royal Randwick"))
				track = "Randwick";
			if(track.equals("Rosehill Gardens"))
				track = "Rosehill";
			if(track.equals("Canterbury Park"))
				track = "Canterbury";
			String comment = horseEl.findElement(By.cssSelector("td.comments")).getText().trim();
			String fullComment = dateStr + " " + track + ": " + comment;
			HorseIndex ind = indices.get(name);
			if (ind != null)
				meeting.getRaces().get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex()).setPreStewards(fullComment);
		}
	}

}
