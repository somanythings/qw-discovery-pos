package uk.gov.borderforce.qwseizure;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;

public class Seizure {
    public String seizedBy;
    public long when;
    public String summary;

    public Seizure() {
    }

    public Seizure(String seizedBy, long when, CharSequence summary) {
        this.seizedBy = seizedBy;
        this.when = when;
        this.summary = summary.toString();
    }

    @NonNull
    public Calendar currentCal() {
        Calendar newCalendar = GregorianCalendar.getInstance();
        newCalendar.setTimeInMillis(this.when);
        return newCalendar;
    }

    public String whenAsIsoFormat() {
        return isoFormat.format(currentCal().getTime());
    }


    @Override
    public String toString() {

        Calendar whenCal = GregorianCalendar.getInstance();
        whenCal.setTimeInMillis(when);
        return "Seizure{" +
                "seizedBy='" + seizedBy + '\'' +
                ", when='" + isoFormat.format(whenCal.getTime()) + "'"
                +
                ", summary='" + summary + '\'' +
                '}';
    }
}


