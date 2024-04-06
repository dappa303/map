package race.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import race.download.model.review.*;
import race.download.model.speedmap.*;

public class MapProcessor {

	public Boolean process(RvMeeting meeting, SmMeeting smMeeting, HashMap<String, HorseIndex> indices) {
		if (doCheck(meeting, smMeeting)) {
			setSettled(meeting, smMeeting, indices);
			return true;
		} else {
			return false;
		}
	}

	private void setSettled(RvMeeting meeting, SmMeeting smMeeting, HashMap<String, HorseIndex> indices) {
		List<Race> races = meeting.getRaces();
		for (Race race : races) {
			List<SortSpeed> sortHorses = new ArrayList<SortSpeed>();
			List<SmHorse> horses = getMapHorses(smMeeting, race.getNumber());
			for (SmHorse horse : horses) {
				if (!horse.getIsScratched())
					sortHorses.add(new SortSpeed(horse.getName(), horse.getPositionIndex()));
			}
			Collections.sort(sortHorses);
			Integer numHorses = sortHorses.size();
			for (int i = 0; i < numHorses; i++) {
				String name = sortHorses.get(i).getName();
				HorseIndex ind = indices.get(name);
				Integer nameIndex = ind.getHorseIndex();
				Horse horse = race.getHorses().get(nameIndex);
				horse.setExpectedSettle(i + 1);
			}
		}
	}

	private Boolean doCheck(RvMeeting meeting, SmMeeting smMeeting) {
		Boolean isOk = true;
		List<SmRace> smRaces = smMeeting.getRaces();
		for (SmRace smRace : smRaces) {
			Integer raceNum = smRace.getNumber();
			ArrayList<String> starters = getStarters(raceNum, meeting);
			if (starters != null) {
				List<SmHorse> horses = smRace.getHorses();
				for (SmHorse horse : horses) {
					Boolean scratched = horse.getIsScratched();
					String name = horse.getName();
					if (starters.contains(name)) {
						if (scratched) {
							isOk = false;
							System.out.println("R" + raceNum + " " + name + " IS NOT a scratching");
						}
					} else {
						if (!scratched) {
							isOk = false;
							System.out.println("R" + raceNum + " " + name + " IS a scratching");
						}
					}
				}
			}
		}
		return isOk;
	}

	private ArrayList<String> getStarters(Integer num, RvMeeting meeting) {
		ArrayList<String> names = null;
		List<Race> races = meeting.getRaces();
		Integer numRaces = races.size();
		for (int i = 0; i < numRaces; i++) {
			if (races.get(i).getNumber() == num) {
				List<Horse> horses = races.get(i).getHorses();
				names = new ArrayList<>();
				for (Horse horse : horses)
					names.add(horse.getName());
				return names;
			}
		}
		return names;
	}

	private List<SmHorse> getMapHorses(SmMeeting meeting, Integer raceNum) {
		List<SmRace> races = meeting.getRaces();
		for (SmRace race : races) {
			if (race.getNumber() == raceNum)
				return race.getHorses();
		}
		return null;
	}
}
