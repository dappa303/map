package race.download.model.speedmap;


public class SortSpeed  implements Comparable<SortSpeed>{
	String name;
	Integer rating;
	Integer position;
	
	public SortSpeed(String name, Integer mapIndex) {
		this.name = name.toUpperCase();
		this.rating = ((mapIndex % 12) * 4) + (mapIndex / 4);
	}

	public String getName() {
		return name;
	}

	public Integer getPosition() {
		return position;
	}

	public int compareTo(SortSpeed s) {
        return this.rating < s.rating ? 1 : this.rating > s.rating ? -1 : 0;
    }
}
