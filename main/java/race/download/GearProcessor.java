package race.download;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import race.download.model.review.*;

public class GearProcessor {

	private ChromeDriver driver;
	final private Wait<WebDriver> wait;

	public GearProcessor(ChromeDriver driver, Wait<WebDriver> wait) {
		this.driver = driver;
		this.wait = wait;
	}

	public void process(RvMeeting meeting, String url, HashMap<String, HorseIndex> indices) {

		driver.get(url);
		WebElement content = wait.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.cssSelector("div#main-content"));
			}
		});
		List<WebElement> horseEls = content.findElements(By.cssSelector("tr[class$='Row']"));
		for (WebElement horseEl : horseEls) {
			String comment = horseEl.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
			if (!comment.equals("Nil")) {
				String name = horseEl.findElement(By.cssSelector("td.horse")).getText().trim()
						.replaceAll("\\N{Right Single Quotation Mark}", "'");
				if (name.contains("(")) {
					name = name.split("\\(")[0].trim();
				}
				HorseIndex ind = indices.get(name);
				if (ind != null)
					meeting.getRaces().get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex()).setGearChanges(comment);
			}

		}
	}
}
