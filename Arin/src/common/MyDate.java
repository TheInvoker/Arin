package common;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/*
 * This handles some date conversions for MySQL and Android
 */

public final class MyDate {
	
	public static Date getDateFromSQLDate(String sqldate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());   
			Date date = (Date) sdf.parse(sqldate);

			long gmtTime = date.getTime();
			
			TimeZone serverTimeZone = TimeZone.getTimeZone("Europe/London");
			
			long timezoneAlteredTime = gmtTime + TimeZone.getDefault().getRawOffset();
			
			Calendar cSchedStartCal1 = Calendar.getInstance(TimeZone.getDefault());
			cSchedStartCal1.setTimeInMillis(timezoneAlteredTime);
			
	        if (serverTimeZone.inDaylightTime(cSchedStartCal1.getTime())) {
	        	cSchedStartCal1.add(Calendar.MILLISECOND, cSchedStartCal1.getTimeZone().getDSTSavings() * -1);
	        }
			
			return cSchedStartCal1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String dateToString(Date date) {
		Format formatter = new SimpleDateFormat("MMM d yyyy hh:mm a", Locale.getDefault());
		return formatter.format(date);
	}
	
	public static String dateToSQLString(Date date) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return formatter.format(date);
	}
	
	public static String getDateDiff(Date startDate) {
		Date endDate = new Date();
        long diff = endDate.getTime() - startDate.getTime();

        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffSeconds = diff / 1000 % 60;
        
        return String.format(Locale.getDefault(), "%d:%d:%d:%d", diffDays, diffHours, diffMinutes, diffSeconds);
	}
}
