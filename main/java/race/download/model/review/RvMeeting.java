package race.download.model.review;

import java.util.List;
import java.time.LocalDate;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("reviews")
public class RvMeeting {

	@Id
	private ObjectId id;
	private String track;
	private LocalDate date;
	private String rail;
	private Integer condition;
	private Double penotrometer;
	private Double goingStick;
	private Double shear;
	private String weather;
	private Integer numRunners;
	private Integer sectRunners;
	private List<Race> races;
	
	public ObjectId getId() {
		return id;
	}
	public String getTrack() {
		return track;
	}
	public LocalDate getDate() {
		return date;
	}
	public String getRail() {
		return rail;
	}
	public Integer getCondition() {
		return condition;
	}
	public Double getPenotrometer() {
		return penotrometer;
	}
	public Double getGoingStick() {
		return goingStick;
	}
	public Double getShear() {
		return shear;
	}
	public String getWeather() {
		return weather;
	}
	public Integer getNumRunners() {
		return numRunners;
	}
	public Integer getSectRunners() {
		return sectRunners;
	}
	public List<Race> getRaces() {
		return races;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setRail(String rail) {
		this.rail = rail;
	}
	public void setCondition(Integer condition) {
		this.condition = condition;
	}
	public void setPenotrometer(Double penotrometer) {
		this.penotrometer = penotrometer;
	}
	public void setGoingStick(Double goingStick) {
		this.goingStick = goingStick;
	}
	public void setShear(Double shear) {
		this.shear = shear;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public void setNumRunners(Integer numRunners) {
		this.numRunners = numRunners;
	}
	public void setSectRunners(Integer sectRunners) {
		this.sectRunners = sectRunners;
	}
	public void setRaces(List<Race> races) {
		this.races = races;
	}
	@Override
	public String toString() {
		String s = "tr  " + track + " dt " + date + " rl " + rail + " cn " + condition + " pn " + penotrometer + " gs " + goingStick ;
		s += " sh " + shear + " we " + weather + " nr " + numRunners + " sr " + sectRunners + "\n";
		for(Race r: races)
			s += r.toString();
		
		return s;
	}
}

	
