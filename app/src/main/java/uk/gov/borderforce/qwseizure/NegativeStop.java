package uk.gov.borderforce.qwseizure;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;

public class NegativeStop extends Seizure {

    public NegativeStop(String who, long when, CharSequence summary) {
        super(who, when, summary);

    }

    @Override
    public String toString() {
        return "-ve" + whenAsIsoFormat() + " from: " + seizedBy + " " + summary.substring(0, Math.min(40, summary.length()));
    }



}
