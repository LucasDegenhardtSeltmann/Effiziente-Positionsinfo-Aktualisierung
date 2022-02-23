package applications;

import java.io.*; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

import org.pi4.locutil.GeoPosition;

public class scoreNN {
    public static void main(String[] args){
        String inputPath = "data/output/empirical_FP_NN.txt";
        String outputPath = "data/output/MU.empirical_FP_NN_scoreNN.txt";
        GeoPosition calc = null;
        GeoPosition real = null;
        ArrayList<String[]> resultEntries = new ArrayList<>();
        ArrayList<Double> resultErrors = new ArrayList<>();

        File resultFile = new File(inputPath);
		Scanner resultParser;
		try {
			resultParser = new Scanner(resultFile);
		while(resultParser.hasNext()) {
			resultEntries.add(resultParser.nextLine().split(";"));
		}
		resultParser.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

        for(String[] entry: resultEntries) {
            String[] calcTmp = entry[1].split(":")[1].replace("(","").split(",");
            String[] realTmp = entry[0].split(":")[1].replace("(","").split(",");
            calc = new GeoPosition(Double.parseDouble(calcTmp[0]),Double.parseDouble(calcTmp[1]));
            real = new GeoPosition(Double.parseDouble(realTmp[0]),Double.parseDouble(realTmp[1]));
            double dist = Math.round(calc.distance(real) * 10.0) / 10.0;
            resultErrors.add(dist);
        }
        Collections.sort(resultErrors, Collections.reverseOrder());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            double errorMargin = 100.0;
            String errorOutputString = String.format(Locale.GERMAN,"%,.2f",errorMargin);
            writer.write(resultErrors.get(0) + " " + errorOutputString + "%\n");
            for(int i = 0; i < resultErrors.size(); i++) {
                if(i >= 1 && resultErrors.get(i) < resultErrors.get(i-1)){
                    errorMargin = (resultErrors.size() - i) / Double.valueOf(resultErrors.size()) *100;
                    errorOutputString = String.format(Locale.GERMAN,"%,.2f",errorMargin);
                    writer.write(resultErrors.get(i) + " " + errorOutputString + "%\n");
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
