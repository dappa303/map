package race.download.model.review;

import java.util.List;

import dev.morphia.annotations.Entity;
@Entity("horses")
public class Horse {
	private Integer number;
	private String name;
	private String nationality;
	private Integer position;
	private String dnf;
	private String image;
	private Integer barrier;
	private String jockey;
	private String trainer;
	private Boolean hasApprentice;
	private Double weight;
	private Double carriedWeight;
	private Double margin;
	private Double time;
	private Double openingPrice;
	private Double midPrice;
	private Double startingPrice;
	private String preStewards;
	private String postStewards;
	private String gearChanges;
	private String cot;
	private Integer actualSettle;
	private Integer expectedSettle;
	private List<Sectional> sectionals;
	
	public Integer getNumber() {
		return number;
	}
	public String getName() {
		return name;
	}
	public String getNationality() {
		return nationality;
	}
	public Integer getPosition() {
		return position;
	}
	public String getDnf() {
		return dnf;
	}
	public String getImage() {
		return image;
	}
	public Integer getBarrier() {
		return barrier;
	}
	public String getJockey() {
		return jockey;
	}
	public String getTrainer() {
		return trainer;
	}
	public Boolean getHasApprentice() {
		return hasApprentice;
	}
	public Double getWeight() {
		return weight;
	}
	public Double getCarriedWeight() {
		return carriedWeight;
	}
	public Double getMargin() {
		return margin;
	}
	public Double getTime() {
		return time;
	}
	public Double getOpeningPrice() {
		return openingPrice;
	}
	public Double getMidPrice() {
		return midPrice;
	}
	public Double getStartingPrice() {
		return startingPrice;
	}
	public String getPreStewards() {
		return preStewards;
	}
	public String getPostStewards() {
		return postStewards;
	}
	public String getGearChanges() {
		return gearChanges;
	}
	public String getCot() {
		return cot;
	}
	public Integer getActualSettle() {
		return actualSettle;
	}
	public Integer getExpectedSettle() {
		return expectedSettle;
	}
	public List<Sectional> getSectionals() {
		return sectionals;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public void setDnf(String dnf) {
		this.dnf = dnf;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setBarrier(Integer barrier) {
		this.barrier = barrier;
	}
	public void setJockey(String jockey) {
		this.jockey = jockey;
	}
	public void setTrainer(String trainer) {
		this.trainer = trainer;
	}
	public void setHasApprentice(Boolean hasApprentice) {
		this.hasApprentice = hasApprentice;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public void setCarriedWeight(Double carriedWeight) {
		this.carriedWeight = carriedWeight;
	}
	public void setMargin(Double margin) {
		this.margin = margin;
	}
	public void setTime(Double time) {
		this.time = time;
	}
	public void setOpeningPrice(Double openingPrice) {
		this.openingPrice = openingPrice;
	}
	public void setMidPrice(Double midPrice) {
		this.midPrice = midPrice;
	}
	public void setStartingPrice(Double startingPrice) {
		this.startingPrice = startingPrice;
	}
	public void setPreStewards(String preStewards) {
		this.preStewards = preStewards;
	}
	public void setPostStewards(String postStewards) {
		this.postStewards = postStewards;
	}
	public void setGearChanges(String gearChanges) {
		this.gearChanges = gearChanges;
	}
	public void setCot(String cot) {
		this.cot = cot;
	}
	public void setActualSettle(Integer actualSettle) {
		this.actualSettle = actualSettle;
	}
	public void setExpectedSettle(Integer expectedSettle) {
		this.expectedSettle = expectedSettle;
	}
	public void setSectionals(List<Sectional> sectionals) {
		this.sectionals = sectionals;
	}
	@Override
	public String toString() {
		String s = "   " + position + " no " + number + " " + name + " nt " + nationality + " br " + barrier;
		s += " wg " + weight +" cw " + carriedWeight + " j " + jockey + " ap " +  hasApprentice + "\n";
		s += "     tr " + trainer + " mg " + margin + " tm " + time + " im " + image;
		s += " op " + openingPrice + " mp " + midPrice + " sp " + startingPrice;
		s += " es " + expectedSettle + " as " + actualSettle;
		if(postStewards != null)
			s += "\n     Stewards: " + postStewards;
		if(preStewards != null)
			s += "\n     Pre Stewards: " + preStewards;
		if(cot != null)
			s += "\n     COT: " + cot;
		if(gearChanges != null)
			s += "\n     Gear: " + gearChanges;
		s += "\n";
		return s;
	}
}
