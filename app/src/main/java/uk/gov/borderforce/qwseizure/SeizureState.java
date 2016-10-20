package uk.gov.borderforce.qwseizure;


import android.util.Log;

import java.util.Calendar;

import static uk.gov.borderforce.qwseizure.NegativeStopActivity.longDateTimeFormat;
import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;
import static uk.gov.borderforce.qwseizure.SeizedGoodsActivity.NullSeizedGoods;

public class SeizureState extends Seizure {
    public static final SeizedGoods NO_DETAILS = NullSeizedGoods;

    final public Calendar cal;
    final public CharSequence freeText;
    final public Person seizedFrom;
    final public SeizedGoods seizedGoods;
    final public String sealId;

    public SeizureState(Calendar cal, CharSequence freeText, Person seizedFrom, SeizedGoods seizedGoods, String sealId) {
        super("Lance Paine", cal, freeText); //todo probably don't want to have this dependency on seizure here, this is just for expediency right now.
        this.cal = cal;
        this.freeText = freeText;
        this.seizedFrom = seizedFrom;
        this.seizedGoods = seizedGoods;
        this.sealId = sealId;
        Log.d("SeizureState", summaryText());
    }

    public String summaryText() {
        String calform = longDateTimeFormat.format(cal.getTime());
        return "seizure @ " + calform + " " + freeText + " " + seizedFrom + " " + seizedGoods + " Seal: " + sealId;
    }

    public SeizureState copyOnTimeChange(int hourOfDay, int minute) {
        Calendar newCalendar = (Calendar) this.cal.clone();
        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCalendar.set(Calendar.MINUTE, minute);
        return new SeizureState(newCalendar, this.freeText, this.seizedFrom, seizedGoods, sealId);
    }

    public SeizureState copyOnDateChange(int day, int month, int year) {
        Calendar newCalendar = (Calendar) this.cal.clone();
        newCalendar.set(Calendar.DAY_OF_MONTH, day);
        newCalendar.set(Calendar.MONTH, month);
        newCalendar.set(Calendar.YEAR, year);
        return new SeizureState(newCalendar, this.freeText, this.seizedFrom, seizedGoods, sealId);
    }

    public SeizureState copyOnTextChange(CharSequence newString) {
        return new SeizureState(this.cal, newString, this.seizedFrom, seizedGoods, sealId);
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


    public SeizureState copyOnSealChange(String sealId) {
        return new SeizureState(this.cal, this.freeText, this.seizedFrom, this.seizedGoods, sealId);
    }

    @Override
    public String toString() {
        return "SeizureState{" +
                "cal=" + isoFormat.format(cal.getTime()) +
                ", freeText=" + freeText +
                ", seizedFrom=" + seizedFrom +
                ", seizedGoods=" + seizedGoods +
                ", sealId='" + sealId + '\'' +
                '}';
    }

    public SeizureState copyOnPersonChange(String mrtz) {
    return new SeizureState(this.cal, this.freeText, Person.fromMrtzJson(mrtz), this.seizedGoods, sealId);
    }

    public SeizureState copyOnGoodsQuantityChange(int newVal) {
        return new SeizureState(this.cal, this.freeText, this.seizedFrom, this.seizedGoods.copyOnQuantity(newVal), sealId);
    }

    public SeizureState copyOnGoodsTypeChange(String currentType) {
        return new SeizureState(this.cal, this.freeText, this.seizedFrom, new SeizedGoods(currentType, this.seizedGoods.quantity, ""), sealId);
    }

}
