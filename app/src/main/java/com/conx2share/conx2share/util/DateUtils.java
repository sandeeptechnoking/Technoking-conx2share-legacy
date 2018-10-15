package com.conx2share.conx2share.util;

import android.util.Log;

import com.conx2share.conx2share.Conx2ShareApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final String TAG = DateUtils.class.getSimpleName();

    public static final String DOB_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DOB_LOCAL_FORMAT = "MMM dd, yyyy";

    public static String getTimeDifference(Date date) {

        if (date != null) {
            final int HOUR_IN_DAY = 24;
            final int DAYS_IN_WEEK = 7;

            long diff = Math.abs(new Date().getTime() - date.getTime());
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            minutes = minutes - hours * 60;
            long days = hours / 24;

            if (hours < HOUR_IN_DAY) {
                return String.valueOf(hours).concat("h ").concat(String.valueOf(minutes).concat("m"));
            } else if (days < DAYS_IN_WEEK) {
                return String.valueOf(days).concat("d");
            } else {
                SimpleDateFormat simpleDate = new SimpleDateFormat(DOB_LOCAL_FORMAT, Locale.CANADA);
                return simpleDate.format(date);
            }
        } else {
            return "";
        }

    }

    public static String getDateAsDayMonthYear(String date) {
        String formattedDate = "";
        try {
            DateFormat dobServerFormat = new SimpleDateFormat(DOB_SERVER_FORMAT);
            DateFormat dobLocalFormat = new SimpleDateFormat(DOB_LOCAL_FORMAT);
            Date originalAsDate = dobServerFormat.parse(date);
            formattedDate = dobLocalFormat.format(originalAsDate);
        } catch (ParseException pe) {
            Log.e(TAG, "Parse exception while trying to format " + date, pe);
        }
        return formattedDate;
    }

    public static String getLocalDateTime(String dateTime) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(dateTime);
            cal.setTime(date);

            String localDate = android.text.format.DateFormat.getDateFormat(Conx2ShareApplication.getInstance())
                    .format(date);
            String localTime = android.text.format.DateFormat.getTimeFormat(Conx2ShareApplication.getInstance())
                    .format(date);

            return localDate + " " + localTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }

    }

    public static String getLocalDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        String localDate = android.text.format.DateFormat.getDateFormat(Conx2ShareApplication.getInstance()).format
                (cal.getTimeInMillis());
        return localDate;
    }

    public static String getLocalTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        String localTime = android.text.format.DateFormat.getTimeFormat(Conx2ShareApplication.getInstance()).format
                (cal.getTimeInMillis());
        return localTime;
    }

    public static String getFormattedLocalDate(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
        return format.format(new Date(millis));
    }

    public static String getFormattedLocalTime(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("KK:mm a", Locale.getDefault());
        return format.format(new Date(millis));
    }

    public static String getFormattedLocalDate(String dateRes) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(dateRes);
            cal.setTime(date);

            return getFormattedLocalDate(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return dateRes;
        }
    }

    public static String getFormattedLocalTime(String time) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(time);
            cal.setTime(date);

            return getFormattedLocalTime(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static String getIsoDateTime(long dateMillis, long timeMillis) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTimeInMillis(dateMillis);

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTimeInMillis(timeMillis);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String isoDate = sdfDate.format(dateCal.getTime());

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String isoTime = sdfTime.format(timeCal.getTime());

        String timezoneOffset = new SimpleDateFormat("Z").format(Calendar.getInstance().getTime());

        return isoDate + " " + isoTime + " " + timezoneOffset;
    }

    public static String getLocalDate(String dateTime) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(dateTime);
            cal.setTime(date);

            String localDate = android.text.format.DateFormat.getDateFormat(Conx2ShareApplication.getInstance())
                    .format(date);

            return localDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static String getLocalTime(String dateTime) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(dateTime);
            cal.setTime(date);

            String localTime = android.text.format.DateFormat.getTimeFormat(Conx2ShareApplication.getInstance())
                    .format(date);

            return localTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static long isoDateTimeToMillis(String isoDateTime) {
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setCalendar(cal);
            Date date = sdf.parse(isoDateTime);
            cal.setTime(date);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getDateMillis(String dateTime) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setTimeZone(timeZone);
            cal.setTimeInMillis(sdf.parse(dateTime).getTime());
            cal.setTimeZone(timeZone);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getTimeMillis(String dateTime) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setTimeZone(timeZone);
            cal.setTimeInMillis(sdf.parse(dateTime).getTime());
            cal.setTimeZone(timeZone);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getDateTimeMillis(String dateTime) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat(DOB_SERVER_FORMAT);
            sdf.setTimeZone(timeZone);
            cal.setTimeInMillis(sdf.parse(dateTime).getTime());
            cal.setTimeZone(timeZone);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
