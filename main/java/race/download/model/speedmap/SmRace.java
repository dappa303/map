package race.download.model.speedmap;

import java.util.List;
import dev.morphia.annotations.Entity;

@Entity("speedmapRaces")
public class SmRace {

	private Integer number;
	private String details;
	private String raceClass;
	private String pace;
	private String condition;
	private List<SmHorse> horses;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getRaceClass() {
		return raceClass;
	}

	public void setRaceClass(String raceClass) {
		this.raceClass = raceClass;
	}

	public String getPace() {
		return pace;
	}

	public void setPace(String pace) {
		this.pace = pace;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<SmHorse> getHorses() {
		return horses;
	}

	public void setHorses(List<SmHorse> horses) {
		this.horses = horses;
	}

	@Override
	public String toString() {
		return "\n  Race number=" + number + ", details=" + details + ", raceClass=" + raceClass + ", pace=" + pace
				+ ", condition=" + condition + ", horses=" + horses;
	}

}
