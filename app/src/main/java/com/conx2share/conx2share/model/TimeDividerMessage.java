package com.conx2share.conx2share.model;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeDividerMessage extends Message {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
    private static final long TWO_DAY = ONE_DAY * 2;
    private static final String MSG_DATE_FORMAT = "MMM dd, hh:mm a";

    private Date timeToDisplay;

    public TimeDividerMessage(Date timeToDisplay, int nextMsgId) {
        this.timeToDisplay = timeToDisplay;
        super.setId(nextMsgId);
    }

    public String getTimeToDisplay() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (currentDate.getTime() - timeToDisplay.getTime() < ONE_DAY) {
            dateFormat.applyPattern("'Today at 'hh:mm a");
        } else if (currentDate.getTime() - timeToDisplay.getTime() < TWO_DAY) {
            dateFormat.applyPattern("'Yesterday at 'hh:mm a");
        } else {
            dateFormat.applyPattern(MSG_DATE_FORMAT);
        }
        return dateFormat.format(timeToDisplay);
    }

}
