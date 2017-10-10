package com.cowbell.cordova.geofence;

import com.google.gson.annotations.Expose;

public class TimeWindow {
    @Expose public String startDate;
    @Expose public int interval;
    @Expose public String startTime;
    @Expose public String endTime;
	@Expose public boolean[] weekDays;
}
