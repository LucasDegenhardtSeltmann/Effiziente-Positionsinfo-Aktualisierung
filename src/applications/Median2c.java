package applications;

import java.io.*; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.pi4.locutil.GeoPosition;

public class Median2c {
    public static void main(String[] args){
        String inputPath = "data/output/model_FP_KNN_2c.txt";
        String outputPath = "data/output/2c_median_model.txt";
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
        int aktError = 1;
        int aktDurchlauf = 1;
        int aktK = 1;
        Double[][][] res = new Double[8][10][46];
        for(Double dist: resultErrors) {
            if(aktError > 46){
                aktError = 1;
                if(aktK >= 8){
                    aktK = 1;
                    aktDurchlauf++;
                }else{
                    aktK++;
                }
            }
            res[aktK-1][aktDurchlauf-1][aktError-1] = dist;
            aktError++;
        }
        ArrayList<Double> tempmedian = new ArrayList<>();
        ArrayList<Double> finalMedian = new ArrayList<>();
        Double tempCalcMedian;
        for( int k = 0; k<res.length; k++){
            for (int i = 0; i<res[k][0].length; i++){
                for(int j = 0; j<res[k].length;j++){
                    tempmedian.add(res[k][j][i]);
                }
                Collections.sort(tempmedian, Collections.reverseOrder());
                tempCalcMedian = (tempmedian.get(4)+tempmedian.get(5))/2;
                finalMedian.add(tempCalcMedian);
                tempmedian.clear();
            }
            aktK = k+1;
            File file = new File(outputPath);
		    try (FileWriter fw = new FileWriter(file, true)){
                if(!file.exists()) {
                    file.createNewFile();
                }
                fw.write("k="+aktK+"\n");
                for(int i = 0; i < finalMedian.size(); i++) {
                    fw.write(finalMedian.get(i) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finalMedian.clear();
        }
    }
}
