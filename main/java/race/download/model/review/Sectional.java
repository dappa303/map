package race.download.model.review;

import java.text.DecimalFormat;

import dev.morphia.annotations.Entity;
@Entity("sectionals")
public class Sectional {
	private Boolean isHorse;
	private Integer distance;
	private Double sect;
	private Double split;
	private Integer position;
	private Integer sectType;
	private Integer sectRating;
	private Integer splitType;
	private Integer splitRating;
	
	public Sectional() {
		//set defaults;
		this.sectType = 1;
		this.splitType = 1;
	}

	public Boolean getIsHorse() {
		return isHorse;
	}

	public void setIsHorse(Boolean isHorse) {
		this.isHorse = isHorse;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Double getSect() {
		return sect;
	}

	public void setSect(Double sect) {
		this.sect = sect;
	}

	public Double getSplit() {
		return split;
	}

	public void setSplit(Double split) {
		this.split = split;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getSectType() {
		return sectType;
	}

	public Integer getSectRating() {
		return sectRating;
	}

	public Integer getSplitType() {
		return splitType;
	}

	public Integer getSplitRating() {
		return splitRating;
	}

	public void setSectType(Integer sectType) {
		this.sectType = sectType;
	}

	public void setSectRating(Integer sectRating) {
		this.sectRating = sectRating;
	}

	public void setSplitType(Integer splitType) {
		this.splitType = splitType;
	}

	public void setSplitRating(Integer splitRating) {
		this.splitRating = splitRating;
	}

	@Override
	public String toString() {
		DecimalFormat df1 = new DecimalFormat(" 000.00 ");
		DecimalFormat df2 = new DecimalFormat(" 00.00 ");
		DecimalFormat df3 = new DecimalFormat(" 00 ");
		DecimalFormat df4 = new DecimalFormat(" 0000 ");
		DecimalFormat df5 = new DecimalFormat(" 000 ");
		String str = "    " + df1.format(sect) + " " + df2.format(split) + " pos " + df3.format(position) + " dist " + df4.format(distance);
		if(sectRating != null) {
				str +=  " sct " + sectType + " scr " + df5.format(sectRating)+ " spt " + splitType + " spr " +df5.format(splitRating);
		}
		str += System.lineSeparator();
		return str;
	}
}
