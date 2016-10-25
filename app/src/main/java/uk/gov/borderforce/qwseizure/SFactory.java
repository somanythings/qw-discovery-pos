package uk.gov.borderforce.qwseizure;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SFactory {
    final static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy/dd/MM HH:mm", Locale.ENGLISH);


    public static Seizure Negative(String who, long when, CharSequence summary) {
        return new NegativeStop(who, when, summary);
    }
}
