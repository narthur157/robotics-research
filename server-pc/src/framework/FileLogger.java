package framework;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Creates file with name as date string, logs data from brick
 * @author Nicholas Arthur
 *
 */
public class FileLogger implements Logger {
	private PrintWriter out;
	
	// creates a string for a most-likely unique file
	// easily sortable in filesystem by alphanumeric ordering
	// this way you can do many test runs without deleting old tests
	// and easily know which tests are which
	// though, I suppose you could also sort by date created
	// but a totally random file name would be odd as well
	// and just over engineering in general
	private String timestampFile() {
		// credit: http://stackoverflow.com/questions/5683728/convert-java-util-date-to-string
		// Create an instance of SimpleDateFormat used for formatting 
		// the string representation of date (month/day/year)
		DateFormat df = new SimpleDateFormat("MM-dd-HH-mm");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		return df.format(today) + "-data.csv";
	}
	
	public FileLogger() throws IOException {
		// set auto flush to true
		out = new PrintWriter(new FileWriter("testRuns/" + timestampFile()), true);
		logln("Time\tLdSpd\tLdPwr\tCtrlPwr\tTrqPwr\tAngle\tStablePwr\tFlag");
	}
	
	public void logln(String s) {
		out.println(s);
	}
	public void close() {
		out.close();
	}
}
