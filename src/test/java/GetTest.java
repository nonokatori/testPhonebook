import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.RestAssured.get;

public class GetTest {

    private Logger logger ;


    @BeforeTest
    public void setLogger() {
        String log4jConfPath = "log.properties";
        PropertyConfigurator.configure(log4jConfPath);
        logger = Logger.getLogger(GetTest.class);

    }

    //негативный тест на создание пользователя с именем некорректной длины
    @Test (description = "POST")
    public void negTestAddEmptyUser() {
        String firstName = "Lisa";
        String sndName = "Meow";
        logger.info("---------------------------------------");
        logger.info("START TEST negTestAddEmptyUser");
        JSONObject user = new JSONObject();
        user.put("firstName", firstName);
        logger.info("SET FIRST NAME " + firstName);
        user.put("lastName", sndName);
        logger.info("SET SECOND NAME " + sndName);

        Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(user.toString()).when().post("http://localhost:8080/users");
        logger.info("POST Response: " + response.asString());
        logger.info("STATUS CODE OF RESPONSE " + response.statusCode());
        logger.info("END LOGGER");
        logger.info("---------------------------------------");
    }

    //негативный тест на изменение длины имени пользователя меньше наименьшего значения
    @Test
    public void negTestChangeNameUser() {

        String newFirstName = "1";
        int id = 1;
        logger.info("---------------------------------------");
        logger.info("START TEST negTestChangeNameUser");
        String uri = "http://localhost:8080/users";
        Response resp = get(uri);
        logger.info("CONNECT TO " + uri);

        JSONArray jsonResponse = new JSONArray(resp.asString());
        JSONObject obj = jsonResponse.getJSONObject(id-1);
        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.body(obj.put("firstName", newFirstName).toString());
        logger.info("SET NEW FIRST NAME " + newFirstName);

        Response response = request.post("http://localhost:8080/users");
        response.then().body("firstName", Matchers.not(newFirstName));
        logger.info("POST Response: " + response.asString());
        logger.info("STATUS CODE OF RESPONSE " + response.statusCode());
        logger.info("END LOGGER");
        logger.info("---------------------------------------");

    }

    //позитивный тест на изменение имени пользователя
    @Test
    public void posTestChangeNameUser() {

        String newFirstName = "Lucky";
        logger.info("---------------------------------------");
        logger.info("START TEST posTestChangeNameUser");
        int id = 1;

        String uri = "http://localhost:8080/users";
        Response resp = get(uri);
        logger.info("CONNECT TO " + uri);

        JSONArray jsonResponse = new JSONArray(resp.asString());
        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.body(jsonResponse.getJSONObject(id-1).put("firstName", newFirstName).toString());
        logger.info("SET NEW FIRST NAME " + newFirstName);
        Response response = request.post("http://localhost:8080/users");

        response.then().body("firstName", Matchers.is(newFirstName));
        logger.info("POST Response: " + response.asString());
        logger.info("STATUS CODE OF RESPONSE " + response.statusCode());
        logger.info("END LOGGER");
        logger.info("---------------------------------------");
    }

    //позитивный тест на удаление пользователя
    @Test (description = "DELETE")
    public void posTestRemoveUser() {
        int id = 14;
        logger.info("---------------------------------------");
        logger.info("START TEST posTestRemoveUser");
        Response response = delete("http://localhost:8080/users/" + id);
        logger.info("USER WITH " + id + " ARE DELETING");
        System.out.println(response.asString());
        int status = response.getStatusCode();
        logger.info("STATUS CODE OF RESPONSE " + response.statusCode());
        Response resp = given().post("http://localhost:8080/users/");
        resp.then().body("id", Matchers.not(id));
        logger.info("POST Response: " + response.asString());
        logger.info("END LOGGER");
        logger.info("---------------------------------------");
    }

    @Test
    public void negTestEmail() {
        int id = 1;
        logger.info("---------------------------------------");
        logger.info("START TEST negTestEmail");

        String uri = "http://localhost:8080/users/1/contacts";
        Response response = get(uri);
        logger.info("CONNECT TO " + uri);
        String newEmail = "123~3@bom.КОТИК";

        JSONArray jsonResponse = new JSONArray(response.asString());
        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.body(jsonResponse.getJSONObject(id-1).put("email", newEmail).toString());
        logger.info("SET NEW EMAIL " + newEmail);

        System.out.println(jsonResponse.getJSONObject(id-1).toString());

        Response resp = request.post("http://localhost:8080/users/1/contacts/1");
        logger.info("POST Response: " + resp.asString());
        resp.then().body("email", Matchers.not(newEmail));
        logger.info("END LOGGER");
        logger.info("---------------------------------------");

    }
}