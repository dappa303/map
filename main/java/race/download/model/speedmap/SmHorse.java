package race.download.model.speedmap;

import dev.morphia.annotations.Entity;

@Entity
public class SmHorse {

	private Integer number;
	private String name;
	private String nationality;
	private String silks;
	private Integer barrier;
	private String trainer;
	private String jockey;
	private String weight;
	private String adjustedWeight;
	private Integer positionIndex;
	private Boolean isScratched;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getSilks() {
		return silks;
	}

	public void setSilks(String silks) {
		this.silks = silks;
	}

	public Integer getBarrier() {
		return barrier;
	}

	public void setBarrier(Integer barrier) {
		this.barrier = barrier;
	}

	public String getTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = trainer;
	}

	public String getJockey() {
		return jockey;
	}

	public void setJockey(String jockey) {
		this.jockey = jockey;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getAdjustedWeight() {
		return adjustedWeight;
	}

	public void setAdjustedWeight(String adjustedWeight) {
		this.adjustedWeight = adjustedWeight;
	}

	public Integer getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(Integer positionIndex) {
		this.positionIndex = positionIndex;
	}

	public Boolean getIsScratched() {
		return isScratched;
	}

	public void setIsScratched(Boolean isScratched) {
		this.isScratched = isScratched;
	}

	@Override
	public String toString() {
		return "\n        Horse [number=" + number + ", name=" + name + ", nationality=" + nationality + ", silks="
				+ silks + ", barrier=" + barrier + ", trainer=" + trainer + ", jockey=" + jockey + ", weight=" + weight
				+ ", adjustedWeight=" + adjustedWeight + ", positionIndex=" + positionIndex + ", isScratched="
				+ isScratched;
	}

}
