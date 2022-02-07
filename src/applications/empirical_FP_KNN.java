package applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import java.lang.Math;


import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

import org.pi4.locutil.*;

/**
 * Example of how to use LocUtil
 * @author mikkelbk
 */

public class empirical_FP_KNN {

	/**
	 * Execute example
	 * @param args
	 */
	public static void main(String[] args) {
		
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace";
		TraceEntry randomOnlineTrace = null;
	
		LinkedList<Double> NN = new LinkedList<Double>();
		GeoPosition A = new GeoPosition(0.0,0.0,0.0);
		GeoPosition B = new GeoPosition(50.0,50.0,0.0);
		PositioningError tempError = new PositioningError(A,B);
		GeoPosition nearestNeighbour = null;
		double aff = 0.0;
		
		
		//Construct parsers
		File offlineFile = new File(offlinePath);
		Parser offlineParser = new Parser(offlineFile);
		System.out.println("Offline File: " +  offlineFile.getAbsoluteFile());
		
		File onlineFile = new File(onlinePath);
		Parser onlineParser = new Parser(onlineFile);
		System.out.println("Online File: " + onlineFile.getAbsoluteFile());
		
		int k = Integer.parseInt(JOptionPane.showInputDialog("Zahl für k eingeben: "))-1;
		LinkedList<GeoPosition> allPos = new LinkedList<GeoPosition>();
		LinkedList<GeoPosition> kNN = new LinkedList<GeoPosition>();
				
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
			
			randomOnlineTrace = onlineTrace.get((int) (Math.random() * onlineTrace.size()) + 1 );
			System.out.println("randomOnlineTrace: " + randomOnlineTrace);
			for(TraceEntry entry: offlineTrace) {
				
				PositioningError positioningError = new PositioningError(entry.getGeoPosition(), randomOnlineTrace.getGeoPosition());
				NN.add(positioningError.getPositioningError());
				
				allPos.add(entry.getGeoPosition());
				
				if(tempError != null && (positioningError.getPositioningError() <= tempError.getPositioningError()) ) {
					
					tempError = positioningError;
					nearestNeighbour = entry.getGeoPosition();// + tempError.getPositioningError();
					aff = tempError.getPositioningError();
				}
			}
				
			NN.sort(null);
			
			for(int i=0; i<allPos.size(); i++){
				for(int j=0; j<=k; j++){
					PositioningError check = new PositioningError(allPos.get(i), randomOnlineTrace.getGeoPosition());
					if(check.getPositioningError() == NN.get(j)) {
						kNN.add(allPos.get(i));
					}
				}
			}
			
						
			for(GeoPosition gp : kNN) {
				write(randomOnlineTrace.getGeoPosition(), gp);
			}
			
			//Iterate the trace generated from the online file
			
			//for(TraceEntry entry: onlineTrace) {
			//Print out coordinates for the collection point and the number of signal strength samples
			//	System.out.println(entry.getGeoPosition().toString() + " - " + entry.getSignalStrengthSamples().size());
			//}
			
			
				System.out.println("NN"+NN);
				
	
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println();
		
	}
	
	public static void write(GeoPosition realPosition, GeoPosition estimatedPosition)
	{
		try {
			File file = new File("empirical_FP_KNN.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			else {
				System.out.println("File already exists");
			}
			
			
			FileWriter fw = new FileWriter(file, true);
			fw.write("RealPosition:"+realPosition);
			fw.write(";");
			fw.write("EstimatedPosition:"+estimatedPosition);
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


