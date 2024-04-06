package race.download.model.track;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("tracks")
public class Track {

	@Id
	private ObjectId id;
	private String name;
	private String rnswName;
	private String rnswDate;
	private String raName;
	private String raDate;
	private String rcomName;
	private String rcomDate;
	private String vidCode;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRnswName() {
		return rnswName;
	}

	public void setRnswName(String rnswName) {
		this.rnswName = rnswName;
	}

	public String getRnswDate() {
		return rnswDate;
	}

	public void setRnswDate(String rnswDate) {
		this.rnswDate = rnswDate;
	}

	public String getRaName() {
		return raName;
	}

	public void setRaName(String raName) {
		this.raName = raName;
	}

	public String getRaDate() {
		return raDate;
	}

	public void setRaDate(String raDate) {
		this.raDate = raDate;
	}

	public String getRcomName() {
		return rcomName;
	}

	public void setRcomName(String rcomName) {
		this.rcomName = rcomName;
	}

	public String getRcomDate() {
		return rcomDate;
	}

	public void setRcomDate(String rcomDate) {
		this.rcomDate = rcomDate;
	}

	public String getVidCode() {
		return vidCode;
	}

	public void setVidCode(String vidCode) {
		this.vidCode = vidCode;
	}

}
