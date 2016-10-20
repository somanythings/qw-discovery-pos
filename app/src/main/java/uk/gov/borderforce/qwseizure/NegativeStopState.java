package uk.gov.borderforce.qwseizure;

import java.util.Calendar;

/**
 * Created by lancep on 20/10/2016.
 */
class NegativeStopState {
    final public Calendar cal;
    final public CharSequence freeText;

    public NegativeStopState(Calendar cal, CharSequence freeText) {
        this.cal = cal;
        this.freeText = freeText;
    }

    public String summaryText() {
        String calform = NegativeStopActivity.longDateTimeFormat.format(cal.getTime());
        return "-ve stop @ " + calform + " " + freeText;
    }

    public NegativeStopState copyOnTimeChange(int hourOfDay, int minute) {
        Calendar newCalendar = (Calendar) this.cal.clone();
        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCalendar.set(Calendar.MINUTE, minute);
        return new NegativeStopState(newCalendar, this.freeText);
    }

    public NegativeStopState copyOnDateChange(int day, int month, int year) {
        Calendar newCalendar = (Calendar) this.cal.clone();
        newCalendar.set(Calendar.DAY_OF_MONTH, day);
        newCalendar.set(Calendar.MONTH, month);
        newCalendar.set(Calendar.YEAR, year);
        return new NegativeStopState(newCalendar, this.freeText);
    }

    public NegativeStopState copyOnTextChange(CharSequence newString) {
        return new NegativeStopState(this.cal, newString);
    }

    public int year() {
        return cal.get(Calendar.YEAR);
    }

    public int month() {
        return cal.get(Calendar.MONTH);
    }


    public int dayOfMonth() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
