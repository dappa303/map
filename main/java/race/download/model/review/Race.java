package race.download.model.review;

import java.util.List;

import dev.morphia.annotations.Entity;
@Entity("races")
public class Race {
	private String name;
	private Integer number;
	private Integer distance;
	private Integer prizeMoney;
	private Double time;
	private Double last600;
	private Double officialTime;
	private Double officialLast600;
	private String officialComment;
	private String raceClass;
	private Integer condition;
	private Double first400;
	private Double first600;
	private Double first800;
	private String video;
	private String stewardsVideo;
	private Double videoDifference;
	private Boolean isSectional;
	private List<Horse> horses;
	private List<Sectional> sectionals;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public Integer getPrizeMoney() {
		return prizeMoney;
	}
	public void setPrizeMoney(Integer prizeMoney) {
		this.prizeMoney = prizeMoney;
	}
	public Double getTime() {
		return time;
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
	public Double getOfficialTime() {
		return officialTime;
	}
	public void setOfficialTime(Double officialTime) {
		this.officialTime = officialTime;
	}
	public Double getOfficialLast600() {
		return officialLast600;
	}
	public void setOfficialLast600(Double officialLast600) {
		this.officialLast600 = officialLast600;
	}
	public String getOfficialComment() {
		return officialComment;
	}
	public void setOfficialComment(String officialComment) {
		this.officialComment = officialComment;
	}
	public String getRaceClass() {
		return raceClass;
	}
	public void setRaceClass(String raceClass) {
		this.raceClass = raceClass;
	}
	public Integer getCondition() {
		return condition;
	}
	public void setCondition(Integer condition) {
		this.condition = condition;
	}
	public Double getFirst400() {
		return first400;
	}
	public void setFirst400(Double first400) {
		this.first400 = first400;
	}
	public Double getFirst600() {
		return first600;
	}
	public void setFirst600(Double first600) {
		this.first600 = first600;
	}
	public Double getFirst800() {
		return first800;
	}
	public void setFirst800(Double first800) {
		this.first800 = first800;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getStewardsVideo() {
		return stewardsVideo;
	}
	public void setStewardsVideo(String stewardsVideo) {
		this.stewardsVideo = stewardsVideo;
	}
	public Double getVideoDifference() {
		return videoDifference;
	}
	public void setVideoDifference(Double videoDifference) {
		this.videoDifference = videoDifference;
	}
	public Boolean getIsSectional() {
		return isSectional;
	}
	public void setIsSectional(Boolean isSectional) {
		this.isSectional = isSectional;
	}
	public List<Horse> getHorses() {
		return horses;
	}
	public void setHorses(List<Horse> horses) {
		this.horses = horses;
	}
	public List<Sectional> getSectionals() {
		return sectionals;
	}
	public void setSectionals(List<Sectional> sectionals) {
		this.sectionals = sectionals;
	}
	@Override
	public String toString() {
		String s = number + " " + name + " "  + distance + "m $" + prizeMoney + " ot " + officialTime + " ol600 ";
		s += officialLast600 + " tm " + time + " l600 "  + last600 + " com " + officialComment + "\n";
		s += " cl " + raceClass + "\ncn " + condition + " vd " + video + " sv " + stewardsVideo;
		s += " f400 " + first400 + " f600 " + first600 + " f800 " + first800 + "\n";
		for(Horse horse : horses)
			s += horse.toString();
		
		return s;
	}
}
