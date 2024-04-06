package race.download.model.sectionals;

import race.download.model.review.Sectional;
import java.util.List;

public class SectHorse {
	Double time;
	String name;
	Integer actualSettle;
	List<Sectional> sectionals;

	public Double getTime() {
		return time;
	}

	public String getName() {
		return name;
	}

	public List<Sectional> getSectionals() {
		return sectionals;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getActualSettle() {
		return actualSettle;
	}

	public void setActualSettle(Integer actualSettle) {
		this.actualSettle = actualSettle;
	}

	public void setSectionals(List<Sectional> sectionals) {
		this.sectionals = sectionals;
	}

	@Override
	public String toString() {
		String str = "name " + name + System.lineSeparator();
		if (sectionals != null) {
			for (Sectional s : sectionals)
				str += s;
		}
		return str;
	}

}
