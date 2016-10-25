package uk.gov.borderforce.qwseizure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by lancep on 19/10/2016.
 */
public class Person {
    final static Person NullPerson = new Person("", "", "", "", "", "", "", "");

    public String name;
    public String givenNames;
    public String surNames;
    public String documentNumber;
    public String nationality;
    public String dob;
    public String sex;
    public String mrtzJson;

    public Person() {}

    public Person(String name, String givenNames, String surNames, String documentNumber, String nationality, String dob, String sex, String mrtz) {
        this.name = name;
        this.givenNames = givenNames;
        this.surNames = surNames;
        this.documentNumber = documentNumber;
        this.nationality = nationality;
        this.dob = dob;
        this.sex = sex;
        this.mrtzJson = mrtz;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", nationality='" + nationality + '\'' +
                ", dob='" + dob + '\'' +
                ", sex='" + sex + '\'' +
                ", mrtzJson='" + mrtzJson + '\'' +
                '}';
    }

    public static Person fromMrtzJson(String mrtzJson) {
        try {
            JSONObject jo = new JSONObject(mrtzJson);
            String surNames = jo.getString("surNames");
            String givenNames = jo.getString("givenNames");
            String documentNumber = jo.getString("documentNumber");
            String nationalityCountryCode = jo.getString("nationalityCountryCode");
            String dob = jo.getString("dayOfBirth");
            String sex = jo.getString("sex");
            Person newPerson = new Person(givenNames + " " + surNames, givenNames, surNames, documentNumber, nationalityCountryCode, dob, sex, mrtzJson);
            return newPerson;
        } catch (JSONException je) {
            return NullPerson;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(givenNames, person.givenNames) &&
                Objects.equals(surNames, person.surNames) &&
                Objects.equals(documentNumber, person.documentNumber) &&
                Objects.equals(nationality, person.nationality) &&
                Objects.equals(dob, person.dob) &&
                Objects.equals(sex, person.sex) &&
                Objects.equals(mrtzJson, person.mrtzJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, givenNames, surNames, documentNumber, nationality, dob, sex, mrtzJson);
    }

    public String shortDescription() {
        return sex + " " + givenNames + " " + surNames + " " + dob;
    }
}
