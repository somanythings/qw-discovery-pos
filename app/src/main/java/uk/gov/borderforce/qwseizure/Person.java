package uk.gov.borderforce.qwseizure;

/**
 * Created by lancep on 19/10/2016.
 */
public class Person {
    final public String name;
    final public String nationality;
    final public String dob;
    final public String sex;

    public Person(String name, String nationality, String dob, String sex) {
        this.name = name;
        this.nationality = nationality;
        this.dob = dob;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", dob='" + dob + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
