package com.cowbell.cordova.geofence;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeFilter {

    protected Logger logger;

    public TimeFilter() {
        logger = Logger.getLogger();
    }

    /**
     * Check if geofence notification happened within specified time range.
     * @param gn
     * @return
     */
    public boolean checkTime(GeoNotification gn) {

        logger.log(Log.DEBUG, "Filter geonotification: " + gn.toJson().toString());

        try {
            // Start date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            //ParsePosition pos = new ParsePosition(0);
            Date startDate = sdf.parse(gn.timeWindow.startDate);
            logger.log(Log.DEBUG, "startDate: " + startDate);

            // End date
            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
			if (gn.timeWindow.interval>0)
			{
            c.add(Calendar.DAY_OF_YEAR, gn.timeWindow.interval);
			}
			else
            c.set(9999,11,31);
			Date endDate = c.getTime();
            logger.log(Log.DEBUG, "endDate: " + endDate);

            // Current date
            Date now = new Date();
            logger.log(Log.DEBUG, "now: " + now);

            // Check time range
            if (now.before(startDate) || now.after(endDate)) {
                logger.log(Log.DEBUG, "Geofence out of time range");
                return false;
            }
            else {
                // Check time of day
                Calendar current = Calendar.getInstance();
                current.setTime(new Date());
                
                SimpleDateFormat todf = new SimpleDateFormat("HH:mm", Locale.US);
                Calendar startTime = Calendar.getInstance();
                startTime.setTime(todf.parse(gn.timeWindow.startTime));
                startTime.set(Calendar.YEAR, current.get(Calendar.YEAR));
                startTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
                startTime.set(Calendar.DATE, current.get(Calendar.DATE));
                logger.log(Log.DEBUG, "startTime: " + startTime.getTime());
                
                
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(todf.parse(gn.timeWindow.endTime));
                endTime.set(Calendar.YEAR, current.get(Calendar.YEAR));
                endTime.set(Calendar.MONTH, current.get(Calendar.MONTH));
                endTime.set(Calendar.DATE, current.get(Calendar.DATE));
                logger.log(Log.DEBUG, "endTime: " + endTime.getTime());

                if (now.before(startTime.getTime()) || now.after(endTime.getTime()) || !(gn.timeWindow.weekDays[now.getDay()])) {
                    logger.log(Log.DEBUG, "Geofence out of time range within day");
                    return false;
                }
            }
        } catch (Exception e) {
            logger.log(Log.ERROR, e.toString());
            logger.log(Log.WARN, "Time-related attributes of this geofence are ignored");

            return true;
        }

        return true;
    }
}
