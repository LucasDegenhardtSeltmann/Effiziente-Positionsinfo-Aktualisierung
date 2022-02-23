package applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

import org.pi4.locutil.*;

/**
 * Example of how to use LocUtil
 * @author mikkelbk
 */

public class model_FP_KNN {

	/**
	 * Execute example
	 * @param args
	 */
	public static void main(String[] args) {
		
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace", apPath = "data/MU.AP.positions";

		//Construct parsers
		File offlineFile = new File(offlinePath);
		Parser offlineParser = new Parser(offlineFile);
		
		File onlineFile = new File(onlinePath);
		Parser onlineParser = new Parser(onlineFile);
		
		int k;
		if(args[0] == null){
			k = Integer.parseInt(JOptionPane.showInputDialog("Zahl für k eingeben: "));
		}else{
			k = Integer.parseInt(args[0]);
		}
		
		ArrayList<String[]> apEntries = getAps(apPath);
		//Construct trace generator
		TraceGenerator tg;
		
		try {
			int offlineSize = 110;
			int onlineSize = 1;
			
			tg = new TraceGenerator(offlineParser, onlineParser,offlineSize,onlineSize,apEntries);
			
			//Generate traces from parsed files
			tg.generate();
			
			//Iterate the trace generated from the offline file
			List<TraceEntry> offlineTrace = tg.getOfflineModel();	
			List<TraceEntry> onlineTrace = tg.getOnline();	
			
			
			HashMap<GeoPosition, Double> estimatedPosition = new HashMap<>();
			for(TraceEntry aktRealEntry: onlineTrace){
				for(TraceEntry entry: offlineTrace) {
								
					double dist = 0;
					for(int i=0; i<apEntries.size(); i++) {
						
						//Wenn Offline-Fingerprint Signalmessung von MACAdresse besitzt
						if(entry.getSignalStrengthSamples().containsKey(MACAddress.parse(apEntries.get(i)[0]))) {
							double offlineTraceSS = entry.getSignalStrengthSamples().getAverageSignalStrength(MACAddress.parse(apEntries.get(i)[0]));
							
						//Wenn Offline-Fingerprint Signalmessung von MACAdresse besitzt und Online-Fingerprint Signalmessung von MACAdresse besitzt
							if(aktRealEntry.getSignalStrengthSamples().containsKey(MACAddress.parse(apEntries.get(i)[0]))) {
								double onlineTraceSS = aktRealEntry.getSignalStrengthSamples().getAverageSignalStrength(MACAddress.parse(apEntries.get(i)[0]));
					
									dist = dist+((onlineTraceSS-(offlineTraceSS))*(onlineTraceSS-(offlineTraceSS)));
							}

						}
						//Wenn Offline-Fingerprint keine Signalmessung von MACAdresse besitzt, aber Online-Fingerprint Signalmessung von MACAdresse hat
						else if(aktRealEntry.getSignalStrengthSamples().containsKey(MACAddress.parse(apEntries.get(i)[0]))) {
							double onlineTraceSS = aktRealEntry.getSignalStrengthSamples().getAverageSignalStrength(MACAddress.parse(apEntries.get(i)[0]));
							
							dist = dist+((onlineTraceSS-(-90.0))*(onlineTraceSS-(-90.0)));
						}
						
					}
					dist = Math.sqrt(dist);
					estimatedPosition.put(entry.getGeoPosition(), dist);
				}		
				nearestNeighbour(aktRealEntry, estimatedPosition, k);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	 
	public static ArrayList<String[]> getAps(String apPath){
		ArrayList<String[]> apEntries= new ArrayList<>();
		File apFile = new File(apPath);
		Scanner apParser;
		try {
			apParser = new Scanner(apFile);
			apParser.nextLine();
		while(apParser.hasNext()) {
			apEntries.add(apParser.nextLine().split(" "));
		}
		apParser.close();
		return apEntries;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static void write(GeoPosition realPosition, GeoPosition estimatedPosition)
	{
		File file = new File("data/output/model_FP_KNN_2c.txt");
		try (FileWriter fw = new FileWriter(file, true)){
			if(!file.exists()) {
				file.createNewFile();
			}
			fw.write("RealPosition:"+realPosition);
			fw.write(";");
			fw.write("EstimatedPosition:"+estimatedPosition);
			fw.write(System.getProperty( "line.separator" ));
		}
		catch(IOException e)
	    {
	        e.printStackTrace();
	     }
	}
	
	public static void nearestNeighbour(TraceEntry realPosition, HashMap<GeoPosition, Double> estimatedPositionsMap, int k) {
	
		
		HashMap<GeoPosition, Double> kNN = new LinkedHashMap<GeoPosition, Double>();
		
		GeoPosition [] aEstimatedPositionsMap = new GeoPosition[estimatedPositionsMap.size()];
		double[] aEukDist = new double[estimatedPositionsMap.size()];
		
		int index = 0;
		for (java.util.Map.Entry<GeoPosition, Double> entry : estimatedPositionsMap.entrySet()) {
			aEstimatedPositionsMap[index] = entry.getKey();
			aEukDist[index] = entry.getValue();
		    index++;
		}
		
		Arrays.sort(aEukDist);
		
		double x=0;
		double y=0;
		double z=0;
		
		for(java.util.Map.Entry<GeoPosition, Double> entry : estimatedPositionsMap.entrySet()) {
			for(int i=0; i<k; i++) {
				if(entry.getValue() == aEukDist[i]) {
					kNN.put(entry.getKey(),entry.getValue());
					x=x+entry.getKey().getX();
					y=y+entry.getKey().getY();
					z=z+entry.getKey().getZ();
				}
			}
		}
		
		GeoPosition estimatedPosition = new GeoPosition((x)/k , (y)/k , (z)/k);
		write(realPosition.getGeoPosition(), estimatedPosition);
		
	}

}


