package race.download.model.review;

public class HorseIndex {
	

	private int raceIndex;
	private int horseIndex;

	public HorseIndex(int r, int h) {
		this.raceIndex = r;
		this.horseIndex = h;
	}

	public int getRaceIndex() {
		return raceIndex;
	}

	public int getHorseIndex() {
		return horseIndex;
	}
	@Override
	public String toString() {
		return "HorseIndex [raceIndex=" + raceIndex + ", horseIndex=" + horseIndex + "]";
	}
}
