package com.idn0phl3108ed43d22s30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ActivityCalendar extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    CalendarView calendar;
    Calendar currentCalender = Calendar.getInstance();
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    public TextView txtMonth;
    private CompactCalendarView compactCalendarView;
    final List<String> mutableBookings = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ConfigView();
    }

    private void ConfigView() {
        txtMonth = (TextView) findViewById(R.id.txtMonth);
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        txtMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        ListView bookingsListView = (ListView) findViewById(R.id.bookings_listview);
        final ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, mutableBookings);
        bookingsListView.setAdapter(adapter);
        //Set tittle
        Event ev1 = new Event(Color.GREEN, 1485690544,"");
        compactCalendarView.addEvent(ev1);
        Event ev2 = new Event(Color.GREEN, 1485604144);
        compactCalendarView.addEvent(ev2);
        List<Event> events = compactCalendarView.getEvents(1433701251000L);
        Log.d(TAG, "Events: " + events);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // On day click
                txtMonth.setText(dateFormatForMonth.format(dateClicked));
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                txtMonth.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }

    private void loadEventsForYear(int year) {
        addEvents(Calendar.DECEMBER, year);
        addEvents(Calendar.AUGUST, year);
    }

    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.MONTH, Calendar.AUGUST);
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }
        Log.d(TAG, "Events for Aug with simple date formatter: " + dates);
        Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
    }

    private void addEvents(int month, int year) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            compactCalendarView.addEvents(events);
        }
    }

    //List view evnet
    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if (day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}

