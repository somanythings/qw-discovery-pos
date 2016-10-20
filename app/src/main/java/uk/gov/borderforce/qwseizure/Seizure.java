package uk.gov.borderforce.qwseizure;

import java.util.Calendar;

import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;

public class Seizure {
    public String seizedBy;
    public Calendar when;
    public String summary;

    public Seizure() {
    }

    public Seizure(String seizedBy, Calendar when, CharSequence summary) {
        this.seizedBy = seizedBy;
        this.when = when;
        this.summary = summary.toString();
    }

    @Override
    public String toString() {
        return "Seizure{" +
                "seizedBy='" + seizedBy + '\'' +
                ", when='" + isoFormat.format(when.getTime()) + "'"
                +
                ", summary='" + summary + '\'' +
                '}';
    }
}


