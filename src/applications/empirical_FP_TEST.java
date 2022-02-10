package applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


import org.pi4.locutil.io.TraceGenerator;
import org.pi4.locutil.trace.Parser;
import org.pi4.locutil.trace.TraceEntry;

import org.pi4.locutil.*;

/**
 * Example of how to use LocUtil
 * @author mikkelbk
 */

public class empirical_FP_TEST {

	/**
	 * Execute example
	 * @param args
	 */
	public static void main(String[] args) {
		
		String offlinePath = "data/MU.1.5meters.offline.trace", onlinePath = "data/MU.1.5meters.online.trace";
		
	
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
			
			GeoPosition onlineTest = new GeoPosition(13.46, 7.85, 0.0);
			GeoPosition test1 = new GeoPosition(12.5, 8.0, 0.0);
			GeoPosition test2 = new GeoPosition(14.0, 8.0, 0.0);
			GeoPosition test3 = new GeoPosition(12.5, 6.5, 0.0);
			
			
			PositioningError positioningError1 = new PositioningError(test1, onlineTest);
			PositioningError positioningError2 = new PositioningError(test2, onlineTest);
			PositioningError positioningError3 = new PositioningError(test3, onlineTest);
			
			
			System.out.println("onlineTest: " + onlineTest);
			System.out.println("test1: " + positioningError1);
			System.out.println("test2: " + positioningError2);
			System.out.println("test3: " + positioningError3);
			
			
			
			
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
			File file = new File("data/output/empirical_FP_N.txt");
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


