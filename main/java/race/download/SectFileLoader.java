package race.download;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import race.download.model.review.*;
import race.download.model.sectionals.*;

public class SectFileLoader {
	
	private final String baseFileName = "C:/Users/dappa/sectionals/repair<CODE>.txt";
	
	public SectMeeting process(String url) {
		SectMeeting meeting = new SectMeeting();
		ArrayList<SectRace> races = new ArrayList<SectRace>();
		ArrayList<SectHorse> horses = new ArrayList<SectHorse>();
		SectRace race = new SectRace();
		ArrayList<String> lines = readFile(getFileName(url));
		for(String line : lines) {
			if(line.startsWith("*")) {
				if(!horses.isEmpty()) {
					race.setHorses(horses);
					races.add(race);
					horses = new ArrayList<SectHorse>();
					race = new SectRace();
				}
				String [] split = line.split(",");
				race.setNumber(Integer.valueOf(split[1]));
				race.setDistance(Integer.valueOf(split[2]));
			}else {
				horses.add(getHorse(line));
			}
			
		}
		race.setHorses(horses);
		races.add(race);
		meeting.setRaces(races);
		return meeting;
	}
	
	private SectHorse getHorse(String line) {
		SectHorse horse = new SectHorse();
		ArrayList<Sectional> sects = new ArrayList<Sectional>();
		String [] split = line.split(",");
		String name = split[0];
		horse.setName(name.replaceAll("\\N{Right Single Quotation Mark}", "'"));
		for(int i = 1;i < split.length; i++) {
			Sectional sect = new Sectional();
			String [] splitSect = split[i].split("\\[|\\]");
			sect.setSect(getTime(splitSect[0]));
			sect.setPosition(Integer.valueOf(splitSect[1]));
			sect.setSectType(1);
			sect.setSplitType(1);
			sect.setIsHorse(true);
			sects.add(sect);
		}
		horse.setSectionals(sects);
		horse.setActualSettle(sects.get(1).getPosition());
		return horse;
	}
	
	private String getFileName(String url) {
		String code = url.split("meeting/")[1].trim();
		return baseFileName.replaceFirst("<CODE>", code);
	}
	
	private ArrayList<String> readFile(String path) {
		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}
	
	private Double getTime(String sect) {
		String[] split = sect.split(":");
		return (Double.valueOf(split[0]) * 60.0) + Double.valueOf(split[1]);
	}

}
