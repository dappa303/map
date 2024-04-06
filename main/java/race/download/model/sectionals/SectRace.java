package race.download.model.sectionals;

import race.download.model.review.Sectional;
import java.util.List;
import java.text.DecimalFormat;

public class SectRace {
	Integer number;
	Integer distance;
	Double time;
	Double last600;
	Double first400;
	Double first600;
	Double first800;
	List<SectHorse> horses;
	List<Sectional> raceSectionals;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getDistance() {
		return distance;
	}

	public Double getTime() {
		return time;
	}

	public Double getFirst400() {
		return first400;
	}

	public Double getFirst600() {
		return first600;
	}

	public Double getFirst800() {
		return first800;
	}

	public List<SectHorse> getHorses() {
		return horses;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public Double getLast600() {
		return last600;
	}

	public void setLast600(Double last600) {
		this.last600 = last600;
	}

	public void setFirst400(Double first400) {
		this.first400 = first400;
	}

	public void setFirst600(Double first600) {
		this.first600 = first600;
	}

	public void setFirst800(Double first800) {
		this.first800 = first800;
	}

	public void setHorses(List<SectHorse> horses) {
		this.horses = horses;
	}

	public List<Sectional> getRaceSectionals() {
		return raceSectionals;
	}

	public void setRaceSectionals(List<Sectional> raceSectionals) {
		this.raceSectionals = raceSectionals;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat(" #00.00 ");
		String str = "num " + number + " dist " + distance + " tm " + time + " f4 " + df.format(first400);
		str += " f8 " + df.format(first800) + System.lineSeparator();
		if(raceSectionals != null) {
			for (Sectional s : raceSectionals)
				str += " " + s.toString();
			str += System.lineSeparator();
		}
		if (horses != null) {
			for (SectHorse h : horses)
				str += "  " + h.toString() + System.lineSeparator();
		}
		return str;
	}
}
