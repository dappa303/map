package race.download;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import race.download.model.review.*;

public class PostStewardsProcessor {

	public void process(RvMeeting meeting, HashMap<String, HorseIndex> indices) {

		getComments(meeting, indices);
		getTatics(meeting, indices);
	}

	private void getTatics(RvMeeting meeting, HashMap<String, HorseIndex> indices) {
		List<Race> races = meeting.getRaces();
		
		String horseReg = "R(ACE|ace)\\s*\\d{1,2}:\\s*(.+)\\N{EN DASH}(.+)";
		Pattern horsePattern = Pattern.compile(horseReg);
		ArrayList<String> lines = readFile(false);
		for (String line : lines) {
			if (Pattern.matches(horseReg, line)) {
				Matcher m = horsePattern.matcher(line);
				m.find();
				String name = m.group(2).trim().toUpperCase().replaceAll("\\N{Right Single Quotation Mark}", "'");
				String comment = m.group(3).trim();
				HorseIndex ind = indices.get(name);
				if (ind != null)
					races.get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex()).setCot(comment);
			}
		}
	}

	private void getComments(RvMeeting meeting, HashMap<String, HorseIndex> indices) {

		List<Race> races = meeting.getRaces();
		String raceReg = "^R(ACE|ace)\\s*\\d.*";
		String horseReg = "(.+)\\N{EN DASH}(.+)";
		Pattern horsePattern = Pattern.compile(horseReg);
		ArrayList<String> lines = readFile(true);
		System.out.println("Num lines " + lines.size());
		String name = null;
		String comment = null;
		for (String line : lines) {
			if (!line.matches(raceReg)) {
				if (Pattern.matches(horseReg, line)) {
					System.out.println(line);
					if (name != null) {
						HorseIndex ind = indices.get(name);
						if (ind != null)
							races.get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex()).setPostStewards(comment);
					}
					Matcher m = horsePattern.matcher(line);
					m.find();
					name = m.group(1).trim().toUpperCase().replaceAll("\\N{Right Single Quotation Mark}", "'");
					comment = m.group(2).trim();
					System.out.println(name);
				} else {
					comment = comment + " " + line;
				}
			}
		}
		HorseIndex ind = indices.get(name);
		if (ind != null)
			races.get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex()).setPostStewards(comment);

	}

	private ArrayList<String> readFile(Boolean isReport) {
		ArrayList<String> lines = new ArrayList<String>();
		String path;
		if (isReport)
			path = "C:/Users/dappa/sectionals/report.txt";
		else
			path = "C:/Users/dappa/sectionals/tactics.txt";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
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

}
