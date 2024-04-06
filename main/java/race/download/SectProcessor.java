package race.download;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Wait;
import race.download.model.review.*;
import race.download.model.sectionals.*;

public class SectProcessor {
	private ChromeDriver driver;
	final private Wait<WebDriver> wait;

	public SectProcessor(ChromeDriver driver, Wait<WebDriver> wait) {
		this.driver = driver;
		this.wait = wait;
	}

	public RvMeeting process(RvMeeting meeting, String url, Boolean isWeb, HashMap<String,HorseIndex> indices) {
		SectMeeting sectMeet;
		if (isWeb) {
			SectWebLoader sectWebLoader = new SectWebLoader(driver, wait);
			sectMeet = sectWebLoader.process(url);
		} else {
			SectFileLoader sectFileLoader = new SectFileLoader();
			sectMeet = sectFileLoader.process(url);
		}
		List<SectRace> races = sectMeet.getRaces();
		for (SectRace race : races) {
			addTimes(race);
			addDistances(race);
			reverseSects(race);
			addSplits(race);
			addRaceSects(race);
			addFirst(race, 400);
			addFirst(race, 600);
			addFirst(race, 800);
			addRaceSects(race);

		}
		addSectRatings(sectMeet);
		addSplitRatings(sectMeet);
		addRaceSectType(sectMeet);
		addRaceSplitType(sectMeet);
		addNumRunners(sectMeet);
		merge(meeting,sectMeet,indices);

	//System.out.println(sectMeet);
		return meeting;
	}

	private void merge(RvMeeting meeting, SectMeeting sectMeeting, HashMap<String,HorseIndex> indices) {
		meeting.setSectRunners(sectMeeting.getNumRunners());
		List<Race> races = meeting.getRaces();
		Integer numRaces = races.size();
		for(int i = 0; i < numRaces; i++) {
			Race race = races.get(i);
			SectRace sectRace = getRace(sectMeeting, race.getNumber());
			if(sectRace != null) {
				race.setIsSectional(true);
				race.setTime(sectRace.getTime());
				race.setLast600(sectRace.getLast600());
				race.setFirst400(sectRace.getFirst400());
				race.setFirst600(sectRace.getFirst600());
				race.setFirst800(sectRace.getFirst800());
				race.setSectionals(sectRace.getRaceSectionals());
				List<SectHorse> sectHorses = sectRace.getHorses();
				List<Horse> horses = race.getHorses();
				for(SectHorse sectHorse : sectHorses) {
					Integer hInd = indices.get(sectHorse.getName()).getHorseIndex();
					Horse horse = horses.get(hInd);
					horse.setTime(sectHorse.getTime());
					horse.setActualSettle(sectHorse.getActualSettle());
					horse.setSectionals(sectHorse.getSectionals());
				}
				
			}else {
				race.setIsSectional(false);
			}
		}

	}
	
	private void addNumRunners(SectMeeting meet) {
		List<SectRace> races = meet.getRaces();
		Integer numRunners = 0;
		for(SectRace race : races)
			numRunners += race.getHorses().size();
		meet.setNumRunners(numRunners);
	}

	private void addSplitRatings(SectMeeting meet) {
		Integer numRaces = meet.getRaces().size();
		List<List<SortSect>> raceBests = new ArrayList<List<SortSect>>();
		for (int i = 0; i < numRaces; i++)
			raceBests.add(new ArrayList<SortSect>());
		for (int i = 0; i < 5; i++) {
			List<SortSect> meetSects = new ArrayList<SortSect>();
			SortSect sortSect;
			for (int j = 0; j < numRaces; j++) {
				List<SortSect> raceSects = new ArrayList<SortSect>();
				SectRace race = meet.getRaces().get(j);
				List<SectHorse> horses = race.getHorses();
				Integer numRunners = horses.size();
				Integer sectIndex = horses.get(0).getSectionals().size() - 5 + i;
				for (int k = 0; k < numRunners; k++) {
					sortSect = new SortSect(j, k, sectIndex, horses.get(k).getSectionals().get(sectIndex).getSplit());
					raceSects.add(sortSect);
				}
				Collections.sort(raceSects);
				Long fastest = raceSects.get(0).getTime();
				for (int k = 0; k < numRunners; k++) {
					sortSect = raceSects.get(k);
					if (raceSects.get(k).getTime() == fastest) {
						Integer hInd = sortSect.getHind();
						horses.get(hInd).getSectionals().get(sectIndex).setSplitType(2);
						raceBests.get(j).add(sortSect);
					} else {
						break;
					}

				}
				meetSects.addAll(raceSects);
			}
			Collections.sort(meetSects);
			Long fastest = meetSects.get(0).getTime();
			Integer numRunners = meetSects.size();
			for (int k = 0; k < numRunners; k++) {
				sortSect = meetSects.get(k);
				if (meetSects.get(k).getTime() == fastest) {
					Integer rInd = sortSect.getRind();
					Integer hInd = sortSect.getHind();
					Integer sInd = sortSect.getSind();
					meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSplitType(3);
				} else {
					break;
				}
			}
			Long time = fastest;
			Integer pos = 1;
			for (int k = 0; k < numRunners; k++) {
				sortSect = meetSects.get(k);
				Integer rInd = sortSect.getRind();
				Integer hInd = sortSect.getHind();
				Integer sInd = sortSect.getSind();
				Long currentTime = sortSect.getTime();
				if (currentTime != time)
					pos = k + 1;
				meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSplitRating(pos);
			}
		}
		List<SortSect> meetingBests = new ArrayList<SortSect>();
		for (int i = 0; i < numRaces; i++) {
			List<SortSect> bestRace = raceBests.get(i);
			Collections.sort(bestRace);
			Long fastest = bestRace.get(0).getTime();
			for (int j = 0; j < bestRace.size(); j++) {
				SortSect sortSect = bestRace.get(j);
				Long time = sortSect.getTime();
				if (time == fastest) {
					Integer rInd = sortSect.getRind();
					Integer hInd = sortSect.getHind();
					Integer sInd = sortSect.getSind();
					meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSplitType(4);
					meetingBests.add(sortSect);
				} else {
					break;
				}
			}
		}
		Collections.sort(meetingBests);
		Long fastest = meetingBests.get(0).getTime();
		for (int i = 0; i < meetingBests.size(); i++) {
			SortSect sortSect = meetingBests.get(i);
			Long time = sortSect.getTime();
			if (time == fastest) {
				Integer rInd = sortSect.getRind();
				Integer hInd = sortSect.getHind();
				Integer sInd = sortSect.getSind();
				meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSplitType(5);
			} else {
				break;
			}
		}
	}

	private void addRaceSectType(SectMeeting meet) {
		List<SectRace> races = meet.getRaces();
		Integer numRaces = races.size();
		for (int i = 0; i < 5; i++) {
			List<SortSect> sects = new ArrayList<SortSect>();
			for (int j = 0; j < numRaces; j++) {
				List<Sectional> raceSects = races.get(j).getRaceSectionals();
				Integer sInd = raceSects.size() - 5 + i;
				sects.add(new SortSect(j, null, sInd, raceSects.get(sInd).getSect()));
			}
			Collections.sort(sects);
			Long fastest = sects.get(0).getTime();
			for (int k = 0; k < numRaces; k++) {
				SortSect sortSect = sects.get(k);
				Integer rInd = sortSect.getRind();
				Integer sectInd = sortSect.getSind();
				Sectional sect = races.get(rInd).getRaceSectionals().get(sectInd);
				if (sects.get(k).getTime() == fastest) {
					sect.setSectType(3);
				}
				sect.setSectRating(1);
			}
		}
	}

	private void addRaceSplitType(SectMeeting meet) {
		List<SectRace> races = meet.getRaces();
		List<SortSect> meetingBests = new ArrayList<SortSect>();
		Integer numRaces = races.size();
		for (int i = 0; i < 5; i++) {
			List<SortSect> sects = new ArrayList<SortSect>();
			for (int j = 0; j < numRaces; j++) {
				List<Sectional> raceSects = races.get(j).getRaceSectionals();
				Integer numSects = raceSects.size();
				Integer sInd = numSects - 5 + i;
				if (i == 0) {
					List<SortSect> raceBests = new ArrayList<SortSect>();
					for (int k = sInd; k < numSects; k++) {
						raceBests.add(new SortSect(j, null, k, raceSects.get(k).getSplit()));
						raceSects.get(k).setSplitRating(1);
					}
					Collections.sort(raceBests);
					Long fastest = raceBests.get(0).getTime();
					for (int k = 0; k < 5; k++) {
						SortSect sortSect = raceBests.get(k);
						Integer sectInd = sortSect.getSind();
						if (sortSect.getTime() == fastest) {
							raceSects.get(sectInd).setSplitType(2);
							meetingBests.add(sortSect);
						} else {
							break;
						}

					}
				}
				sects.add(new SortSect(j, null, sInd, raceSects.get(sInd).getSplit()));
			}
			Collections.sort(sects);
			Long fastest = sects.get(0).getTime();
			for (int k = 0; k < numRaces; k++) {
				SortSect sortSect = sects.get(k);
				Integer rInd = sortSect.getRind();
				Integer sectInd = sortSect.getSind();
				Sectional sect = races.get(rInd).getRaceSectionals().get(sectInd);
				if (sects.get(k).getTime() == fastest) {
					sect.setSplitType(3);
				} else {
					break;
				}
			}
		}
		Collections.sort(meetingBests);
		Integer numSects = meetingBests.size();
		Long fastest = meetingBests.get(0).getTime();
		for (int k = 0; k < numSects; k++) {
			SortSect sortSect = meetingBests.get(k);
			Integer rInd = sortSect.getRind();
			Integer sectInd = sortSect.getSind();
			Sectional sect = races.get(rInd).getRaceSectionals().get(sectInd);
			if (meetingBests.get(k).getTime() == fastest) {
				sect.setSplitType(5);
			} else {
				break;
			}
		}
	}

	private void addSectRatings(SectMeeting meet) {
		Integer numRaces = meet.getRaces().size();
		for (int i = 0; i < 5; i++) {
			List<SortSect> meetSects = new ArrayList<SortSect>();
			SortSect sortSect;
			for (int j = 0; j < numRaces; j++) {
				List<SortSect> raceSects = new ArrayList<SortSect>();
				SectRace race = meet.getRaces().get(j);
				List<SectHorse> horses = race.getHorses();
				Integer numRunners = horses.size();

				Integer sectIndex = horses.get(0).getSectionals().size() - 5 + i;
				for (int k = 0; k < numRunners; k++) {
					sortSect = new SortSect(j, k, sectIndex, horses.get(k).getSectionals().get(sectIndex).getSect());
					raceSects.add(sortSect);
				}
				Collections.sort(raceSects);
				Long fastest = raceSects.get(0).getTime();
				for (int k = 0; k < numRunners; k++) {
					sortSect = raceSects.get(k);
					if (raceSects.get(k).getTime() == fastest) {
						Integer hInd = sortSect.getHind();
						horses.get(hInd).getSectionals().get(sectIndex).setSectType(2);
					} else {
						break;
					}

				}
				meetSects.addAll(raceSects);
			}
			Collections.sort(meetSects);
			Long fastest = meetSects.get(0).getTime();
			Integer numRunners = meetSects.size();
			for (int k = 0; k < numRunners; k++) {
				sortSect = meetSects.get(k);
				if (meetSects.get(k).getTime() == fastest) {
					Integer rInd = sortSect.getRind();
					Integer hInd = sortSect.getHind();
					Integer sInd = sortSect.getSind();
					meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSectType(3);
				} else {
					break;
				}
			}
			Long time = fastest;
			Integer pos = 1;
			for (int k = 0; k < numRunners; k++) {
				sortSect = meetSects.get(k);
				Integer rInd = sortSect.getRind();
				Integer hInd = sortSect.getHind();
				Integer sInd = sortSect.getSind();
				Long currentTime = sortSect.getTime();
				if (currentTime != time)
					pos = k + 1;
				meet.getRaces().get(rInd).getHorses().get(hInd).getSectionals().get(sInd).setSectRating(pos);
			}
		}
	}

	private void addRaceSects(SectRace race) {
		ArrayList<Sectional> sects = new ArrayList<Sectional>();
		List<SectHorse> horses = race.getHorses();
		Integer numHorses = horses.size();
		Integer numSects = horses.get(0).getSectionals().size();
		Double rTime = race.getTime();
		Double timeTo;
		Sectional sect = new Sectional();
		sect.setDistance(race.getDistance());
		sect.setSect(rTime);
		sect.setPosition(1);

		sect.setIsHorse(false);
		sects.add(sect);
		for (int i = 1; i < numSects; i++) {
			sect = new Sectional();
			for (int j = 0; j < numHorses; j++) {
				SectHorse horse = horses.get(j);
				if (horse.getSectionals().get(i - 1).getPosition() == 1) {
					timeTo = horse.getTime() - horse.getSectionals().get(i).getSect();
					sect.setSect(rTime - timeTo);
					sect.setDistance((numSects - i) * 200);
					sect.setPosition(1);

					sect.setIsHorse(false);
					sects.add(sect);
					break;
				}
			}
		}
		sects.get(numSects - 1).setSplit(sects.get(numSects - 1).getSect());
		for (int i = 0; i < numSects - 1; i++) {
			sects.get(i).setSplit(sects.get(i).getSect() - sects.get(i + 1).getSect());
		}
		race.setLast600(sects.get(numSects - 3).getSect());
		race.setRaceSectionals(sects);
	}

	private void addTimes(SectRace race) {
		List<SectHorse> horses = race.getHorses();
		Integer lastIndex = horses.get(0).getSectionals().size() - 1;
		Integer numHorses = horses.size();
		for (int i = 0; i < numHorses; i++) {
			SectHorse horse = horses.get(i);
			if (i == 0) {
				race.setTime(horse.getSectionals().get(lastIndex).getSect());
			}
			horse.setTime(horse.getSectionals().get(lastIndex).getSect());
		}
	}

	private void addFirst(SectRace race, Integer dist) {
		List<Sectional> sects = race.getRaceSectionals();
		Integer adjustDist = race.getDistance() - sects.size() * 200;
		Double adjustTime = 0.0;
		Integer ind = (dist / 200);
		Double time = race.getTime() - sects.get(ind).getSect();
		if (adjustDist < 0)
			adjustTime = sects.get(ind).getSplit() * (adjustDist / 200.0);
		else
			adjustTime = sects.get(ind - 1).getSplit() * (adjustDist / 200.0);
		time -= adjustTime;

		if (dist == 400) {
			race.setFirst400(time);
		} else if (dist == 600) {
			race.setFirst600(time);
		} else if (dist == 800) {
			race.setFirst800(time);
		}
	}

	private void addSplits(SectRace race) {
		Integer lastSect = race.getHorses().get(0).getSectionals().size() - 1;
		List<SectHorse> horses = race.getHorses();
		for (SectHorse horse : horses) {
			List<Sectional> sects = horse.getSectionals();
			for (int i = 0; i < lastSect; i++)
				sects.get(i).setSplit(sects.get(i).getSect() - sects.get(i + 1).getSect());
			sects.get(lastSect).setSplit(sects.get(lastSect).getSect());
		}

	}

	private void addDistances(SectRace race) {
		Integer numSect = race.getHorses().get(0).getSectionals().size();
		List<SectHorse> horses = race.getHorses();
		for (SectHorse horse : horses) {
			List<Sectional> sects = horse.getSectionals();
			sects.get(0).setDistance(race.getDistance());
			for (int i = 1; i < numSect; i++) {
				sects.get(i).setDistance((numSect - i) * 200);
			}
		}
	}

	private void reverseSects(SectRace race) {
		Integer lastSect = race.getHorses().get(0).getSectionals().size() - 1;
		List<SectHorse> horses = race.getHorses();
		for (SectHorse horse : horses) {
			List<Sectional> sects = horse.getSectionals();
			Double time = sects.get(lastSect).getSect();
			for (int i = lastSect; i > 0; i--) {
				sects.get(i).setSect(time - sects.get(i - 1).getSect());
			}
			sects.get(0).setSect(time);
		}
	}

	private SectRace getRace(SectMeeting meeting, Integer num) {
		List<SectRace> races = meeting.getRaces();
		for (SectRace race : races) {
			if (race.getNumber() == num)
				return race;
		}
		return null;
	}
}
