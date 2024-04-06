package race.download.model.sectionals;

import java.util.List;

public class SectMeeting {
	Integer numRunners;
	List<SectRace> races;
	public Integer getNumRunners() {
		return numRunners;
	}
	public List<SectRace> getRaces() {
		return races;
	}
	public void setNumRunners(Integer numRunners) {
		this.numRunners = numRunners;
	}
	public void setRaces(List<SectRace> races) {
		this.races = races;
	}
	@Override
	public String toString() {
		String str;
		str = "numRunners=" + numRunners + System.lineSeparator();
		for(SectRace r : races ) {
			str += r.toString();
		}
		return str;
	}

}
