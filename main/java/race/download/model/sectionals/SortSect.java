package race.download.model.sectionals;

public class SortSect implements Comparable<SortSect>{
	Integer rind;
	Integer hind;
	Integer sind;
	Long time;
	
	public SortSect(Integer rind, Integer hind, Integer sind, Double dTime) {
		this.rind = rind;
		this.hind = hind;
		this.sind = sind;
		dTime = dTime * 100;
		this.time = dTime.longValue();
	}
	public Integer getRind() {
		return rind;
	}
	public Integer getHind() {
		return hind;
	}
	public Integer getSind() {
		return sind;
	}
	
	public Long getTime() {
		return time;
	}

	public int compareTo(SortSect s) {
        return this.time > s.time ? 1 : this.time < s.time ? -1 : 0;
    }

}
