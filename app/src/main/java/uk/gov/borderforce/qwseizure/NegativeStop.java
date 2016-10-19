package uk.gov.borderforce.qwseizure;

import java.util.Calendar;

import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;

public class NegativeStop extends Seizure {

    public NegativeStop(String who, Calendar when, CharSequence summary) {
        super(who, when, summary);

    }

    @Override
    public String toString() {
        return "-ve" + isoFormat.format(when.getTime()) + " from: " + who + " " + summary.substring(0, Math.min(40, summary.length()));
    }


}
