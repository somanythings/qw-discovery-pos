package uk.gov.borderforce.qwseizure;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static uk.gov.borderforce.qwseizure.NegativeStopActivity.longDateTimeFormat;
import static uk.gov.borderforce.qwseizure.SFactory.isoFormat;
import static uk.gov.borderforce.qwseizure.SeizedGoodsActivity.NullSeizedGoods;

public class SeizureState extends Seizure {

    public String freeText;
    public Person seizedFrom;
    public SeizedGoods seizedGoods;
    public String sealId;

    public SeizureState() {}

    public SeizureState(long cal, String freeText, Person seizedFrom, SeizedGoods seizedGoods, String sealId) {
        super("Lance Paine", cal, freeText); //todo probably don't want to have this dependency on seizure here, this is just for expediency right now.
        this.freeText = freeText.toString();
        this.seizedFrom = seizedFrom;
        this.seizedGoods = seizedGoods;
        this.sealId = sealId;
        Log.d("SeizureState", summaryText());
    }

    public String summaryText() {
        String calform = longDateTimeFormat.format(currentCal().getTime());
        String seizedFromDesc = seizedFromDesc();
        return "seizure @ " + calform + " " + freeText + " from:" + seizedFromDesc + " " + seizedGoods + " Seal: " + sealId;
    }

    public String seizedFromDesc() {
        if (seizedFrom == null) {

            return "";
        }
        return seizedFrom.shortDescription();
    }

    public SeizureState copyOnTimeChange(int hourOfDay, int minute) {
        Calendar newCalendar = currentCal();
        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCalendar.set(Calendar.MINUTE, minute);
        return new SeizureState(newCalendar.getTimeInMillis(), this.freeText, this.seizedFrom, seizedGoods, sealId);
    }

    public SeizureState copyOnDateChange(int day, int month, int year) {
        Calendar newCalendar = currentCal();
        newCalendar.set(Calendar.DAY_OF_MONTH, day);
        newCalendar.set(Calendar.MONTH, month);
        newCalendar.set(Calendar.YEAR, year);
        return new SeizureState(newCalendar.getTimeInMillis(), this.freeText, this.seizedFrom, seizedGoods, sealId);
    }


    public SeizureState copyOnTextChange(String newString) {
        return new SeizureState(when, newString, this.seizedFrom, seizedGoods, sealId);
    }


    public int year() {
        return currentCal().get(Calendar.YEAR);
    }

    public int month() {
        return currentCal().get(Calendar.MONTH);
    }


    public int dayOfMonth() {
        return currentCal().get(Calendar.DAY_OF_MONTH);
    }


    public SeizureState copyOnSealChange(String sealId) {
        return new SeizureState(when, this.freeText, this.seizedFrom, this.seizedGoods, sealId);
    }

    @Override
    public String toString() {
        return "SeizureState{" +
                "cal=" + isoFormat.format(currentCal().getTime()) +
                ", freeText=" + freeText +
                ", seizedFrom=" + seizedFrom +
                ", seizedGoods=" + seizedGoods +
                ", sealId='" + sealId + '\'' +
                "} " + super.toString();
    }

    public SeizureState copyOnPersonChange(String mrtz) {
    return new SeizureState(when, this.freeText, Person.fromMrtzJson(mrtz), this.seizedGoods, sealId);
    }

    public SeizureState copyOnGoodsQuantityChange(int newVal) {
        return new SeizureState(when, this.freeText, this.seizedFrom, this.seizedGoods.copyOnQuantity(newVal), sealId);
    }

    public SeizureState copyOnGoodsTypeChange(String currentType) {
        return new SeizureState(when, this.freeText, this.seizedFrom, new SeizedGoods(currentType, this.seizedGoods.quantity, ""), sealId);
    }

    @NonNull
    String formatShortDate(SeizedGoodsActivity seizedGoodsActivity) {
        return year() + "/" + month()
                + "/" + dayOfMonth();
    }
}
