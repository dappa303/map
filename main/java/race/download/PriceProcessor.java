package race.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import race.download.model.review.*;

public class PriceProcessor {
	
	public void process(RvMeeting meeting, HashMap<String,HorseIndex> indices) {
		ArrayList<String> lines = readFile();
		for(String line : lines) {
			String [] splits = line.split(",");
			HorseIndex ind = indices.get(splits[0]);
			Horse horse = meeting.getRaces().get(ind.getRaceIndex()).getHorses().get(ind.getHorseIndex());
			horse.setOpeningPrice(Double.valueOf(splits[1]));
			horse.setMidPrice(Double.valueOf(splits[2]));
			horse.setStartingPrice(Double.valueOf(splits[3]));
		}
	}
	
	
	private ArrayList<String> readFile() {
		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader reader;
		String path = "C:/Users/dappa/sectionals/prices.txt";

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

}
