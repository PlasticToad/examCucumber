package api.steps;

import api.dataClass.CharacterData;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static api.Specifications.*;
import static io.restassured.RestAssured.given;

public class ApiSteps {
    static Properties prop = new Properties();
    static void propLoad() {
        try(InputStream input = Files.newInputStream(Paths.get("src/test/resources/application.properties"))) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static CharacterData character1 = new CharacterData();
    public static CharacterData character2 = new CharacterData();
    public static void getCharacterData(String id, CharacterData character) {
        specInstall(reqSpec(prop.getProperty("RAMBURL")), respSpec());
        Response req = given()
                .filter(new AllureRestAssured())
                .when()
                .get("/character/"+id)
                .then()
                .log().all()
                .extract()
                .response();
        character.setLocation(new JSONObject(req.getBody().asString()).getJSONObject("location").get("name").toString());
        character.setName(new JSONObject(req.getBody().asString()).get("name").toString());
        character.setSpecies(new JSONObject(req.getBody().asString()).get("species").toString());
        int episode = (new JSONObject(req.getBody().asString()).getJSONArray("episode").length() - 1);
        character.setLastEp(new JSONObject(req.getBody().asString()).getJSONArray("episode").get(episode).toString().replaceAll("[^0-9]", ""));
    }
    @Дано("Получить данные о персонаже с id {string} и о персонаже из последнего эпизода")
    public static void getCharactersData(String id) {
        propLoad();
        getOriginChar(id);
        getLastCharOfLastEp(character1.getLastEp());
    }

    @Step("Get запрос информации о персонаже с id {id}")
    public static void getOriginChar(String id) {
        getCharacterData(id, character1);
    }
    @Step("Get запрос информаци о персонаже из эпизода с id {lEp}")
    public static void getLastCharOfLastEp(String lEp) {
        specInstall(reqSpec(prop.getProperty("RAMBURL")), respSpec());
        Response req = given()
                .filter(new AllureRestAssured())
                .when()
                .get("/episode/"+lEp)
                .then()
                .log().all()
                .extract()
                .response();
        int tempArrId = new JSONObject(req.getBody().asString()).getJSONArray("characters").length() - 1;
        String tempCharId = new JSONObject(req.getBody().asString()).getJSONArray("characters").get(tempArrId).toString().replaceAll("[^0-9]", "");
        getCharacterData(tempCharId, character2);

    }
    @Тогда("Сравнить расу и локацию персонажей")
    public static void checkSpeciesLocation() {
        try {
            Assertions.assertEquals(character1.getLocation(), character2.getLocation());
        } catch (AssertionFailedError e) {
            String errorText = "Локации не совпадают!\n"+
                    character1.getName() + ": " + character1.getLocation() + "\n"+
                    character2.getName() + ": " + character2.getLocation() + "\n";
            sendText(errorText);
        }
        try {
            Assertions.assertEquals(character1.getSpecies(), character2.getSpecies());
        } catch (AssertionFailedError e) {
            String errorText = "Раса не совпадает!\n"+
                    character1.getName() + ": " + character1.getSpecies() + "\n"+
                    character2.getName() + ": " + character2.getSpecies() + "\n";
            sendText(errorText);
        }
    }
    @Attachment(value = "При сравнении не совпали данные:")
    public static String sendText(String message) {
        return message;
    }
    @Дано("Отправить запрос на создание юзера: Имя {string} и Професия {string}")
    @Step("Post запрос на создание пользователя Имя/Работа : {name}/{job}. Проверка ответа")
    public static void createUser(String name, String job) {
        propLoad();
        JSONObject body = null;
        try {
            body = new JSONObject(new String(Files.readAllBytes(Paths.get("src/test/resources/someJson.json"))));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        body.put("name", name);
        body.put("Job", job);
        specInstall(reqSpec(prop.getProperty("REQRESBURL")), respSpec());
        Response req = given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .and()
                .body(body.toString())
                .when()
                .log().all()
                .post("/users")
                .then()
                .log().all()
                .extract()
                .response();
        Assertions.assertEquals(name, req.jsonPath().getString("name"));
        Assertions.assertEquals(job, req.jsonPath().getString("Job"));
    }
}
