package uk.gov.borderforce.qwseizure;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SFactory {
    final static SimpleDateFormat isoFormat = new SimpleDateFormat("YYYY/dd/mm HH:mm");


    public static Seizure Negative(String who, Calendar when, CharSequence summary) {
        return new NegativeStop(who, when, summary);
    }
}
