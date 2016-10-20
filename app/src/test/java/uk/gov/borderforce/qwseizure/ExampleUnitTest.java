package uk.gov.borderforce.qwseizure;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void gson_Serialization() throws Exception {
        String jsonSeizure = "{\"cal\":{\"year\":2016,\"month\":9,\"dayOfMonth\":20,\"hourOfDay\":16,\"minute\":33,\"second\":17},\"freeText\":\"JZ DB SZ GTW\",\"sealId\":\"AB0861331\",\"seizedFrom\":{\"dob\":\"800219\",\"documentNumber\":\"LH813311\",\"givenNames\":\"Mickey Joseph\",\"mrtzJson\":\"\",\"name\":\"\",\"nationality\":\"\",\"sex\":\"\",\"surNames\":\"\"},\"seizedGoods\":{\"quantity\":2000,\"units\":\"cigarettes\",\"what\":\"cigarettes\"},\"summary\":\"\",\"when\":{\"year\":2016,\"month\":9,\"dayOfMonth\":20,\"hourOfDay\":16,\"minute\":33,\"second\":17},\"seizedBy\":\"Lance Paine\"}";
        Gson gson = new Gson();
        SeizureState sg = gson.fromJson(jsonSeizure, SeizureState.class);
        System.out.print(sg);

    }
}