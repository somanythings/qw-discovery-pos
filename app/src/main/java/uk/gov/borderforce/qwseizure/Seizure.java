package uk.gov.borderforce.qwseizure;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;

public class Seizure {
    public final String who;
    public final Calendar when;
    public final String summary;

    public Seizure(String who, Calendar when, CharSequence summary) {
        this.who = who;
        this.when = when;
        this.summary = summary.toString();
    }

    @Override
    public String toString() {
        return isoFormat.format(when.getTime()) + " from: " + who + " " + summary.substring(0, Math.min(40, summary.length()));
    }


}


