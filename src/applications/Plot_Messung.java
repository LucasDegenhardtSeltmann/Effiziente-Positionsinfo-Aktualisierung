package applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.lang.Math;


import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;
import org.pi4.locutil.trace.SignalStrengthSamples;

import org.pi4.locutil.*;

/**
 * Example of how to use LocUtil
 * @author mikkelbk
 */

public class Plot_Messung {

	/**
	 * Execute example
	 * @param args
	 */
	public static void main(String[] args) {
		
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace", apPath = "data/MU.AP.positions";
		TraceEntry randomOnlineTrace = null;
		TraceEntry randomOfflineTrace = null;
		String [] apMac = { "00:14:BF:B1:7C:54",
							"00:16:B6:B7:5D:8F",
							"00:14:BF:B1:7C:57",
							"00:14:BF:B1:97:8D",
							"00:16:B6:B7:5D:9B",
							"00:14:6C:62:CA:A4",
							"00:14:BF:3B:C7:C6",
							"00:14:BF:B1:97:8A",
							"00:14:BF:B1:97:81",
							"00:16:B6:B7:5D:8C",
							"00:11:88:28:5E:E0" };
		
		//Construct parsers
		File offlineFile = new File(offlinePath);
		Parser offlineParser = new Parser(offlineFile);
		System.out.println("Offline File: " +  offlineFile.getAbsoluteFile());
		
		File onlineFile = new File(onlinePath);
		Parser onlineParser = new Parser(onlineFile);
		System.out.println("Online File: " + onlineFile.getAbsoluteFile());
		
		ArrayList<String[]> apEntries= new ArrayList<String[]>();
		File apFile = new File(apPath);
		Scanner apParser;
		try {
			apParser = new Scanner(apFile);
			apParser.nextLine();
		while(apParser.hasNext()) {
			apEntries.add(apParser.nextLine().split(" "));
		}
		apParser.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		
		//Construct trace generator
		TraceGenerator tg;
		
		
		try {
			int offlineSize = 1;
			int onlineSize = 1;
			
			tg = new TraceGenerator(offlineParser, onlineParser,offlineSize,onlineSize);
			
			//Generate traces from parsed files
			tg.generate();
			
			//Iterate the trace generated from the offline file
			List<TraceEntry> offlineTrace = tg.getOffline();	
			List<TraceEntry> onlineTrace = tg.getOnline();	
	
			//randomOnlineTrace = onlineTrace.get((int) (Math.random() * onlineTrace.size()) + 1 );
			randomOnlineTrace = onlineTrace.get(1);
			randomOfflineTrace = offlineTrace.get(7);
		//	System.out.println("randomOnlineTrace: " + randomOnlineTrace);
			System.out.println("randomOfflineTrace: " + randomOfflineTrace);
			
			
				for(int i=0; i<apMac.length; i++) {
					
					//Wenn Offline-Fingerprint Signalmessung von MACAdresse besitzt
					if(randomOfflineTrace.getSignalStrengthSamples().containsKey(MACAddress.parse(apEntries.get(i)[0]))) {
						double offlineTraceSS = randomOfflineTrace.getSignalStrengthSamples().getAverageSignalStrength(MACAddress.parse(apEntries.get(i)[0]));
						GeoPosition gpOfflineTrace = randomOfflineTrace.getGeoPosition();
						GeoPosition gpAPTrace = new GeoPosition(Double.parseDouble(apEntries.get(i)[1]),Double.parseDouble(apEntries.get(i)[2]),Double.parseDouble(apEntries.get(i)[3]));
						PositioningError PE = new PositioningError(gpOfflineTrace, gpAPTrace);
					
						write(offlineTraceSS, PE);
					}
				}
			
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	public static void write(double sS, PositioningError PE)
	{
		try {
			File file = new File("data/output/plot_messung_5.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			else {
				System.out.println("File already exists");
			}
			
			
			FileWriter fw = new FileWriter(file, true);
			fw.write(sS+" "+PE.getPositioningError());
			fw.write(System.getProperty( "line.separator" ));
			fw.close();
		}
		catch(FileNotFoundException e)
	    {
	        System.out.println("File Not Found");
	     }
	    catch(IOException e)
	    {
	        System.out.println("something messed up");
	    }
	}
}


