package race.download.model.speedmap;

import java.util.List;
import java.time.LocalDate;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("speedmaps")
public class SmMeeting {

	@Id
	private ObjectId id;
	private String track;
	private LocalDate date;
	private String rail;
	private List<SmRace> races;
	

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getRail() {
		return rail;
	}

	public void setRail(String rail) {
		this.rail = rail;
	}

	public List<SmRace> getRaces() {
		return races;
	}

	public void setRaces(List<SmRace> races) {
		this.races = races;
	}

	@Override
	public String toString() {
		return "Meeting id=" + id + ", track=" + track + ", date=" + date + ", rail=" + rail + ", races=" + races;
	}

}
