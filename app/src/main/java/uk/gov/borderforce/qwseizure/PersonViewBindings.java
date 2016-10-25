package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PersonViewBindings {
    public final TextView surname;
    public final TextView givenNames;
    public final TextView dob;
    public final TextView documentId;
    public final TextView sex;
    public final TextView nationality;

    public PersonViewBindings(TextView surname, TextView givenNames, TextView dob, TextView documentId, TextView sex, TextView nationality) {
        this.surname = surname;
        this.givenNames = givenNames;
        this.dob = dob;
        this.documentId = documentId;
        this.sex = sex;
        this.nationality = nationality;
    }

    public static PersonViewBindings fromParent(Activity view) {
        return new
                PersonViewBindings(
                (TextView) view.findViewById(R.id.person_sur_name),
                (TextView) view.findViewById(R.id.person_given_name),
                (TextView) view.findViewById(R.id.person_dob),
                (TextView) view.findViewById(R.id.person_document_id),
                (TextView) view.findViewById(R.id.person_sex),
                (TextView) view.findViewById(R.id.person_nationality));
    }
}
