import io.restassured.*;
import io.restassured.http.*;
import io.restassured.response.*;
import org.hamcrest.*;
import org.junit.*;

import java.util.*;

public class testRestAssurePayroll {
    private int empId;

    @Before
    public void setup() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4001;
        empId = 1;
    }

    public Response getEmployeeList() {
        Response response = RestAssured.get("/employees/list");
        System.out.println(response.getBody());
        return response;
    }

    @Test
    public void Check_InsertEmp_Method() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\":\"abc\",\"salary\":\"700000\"}")
                .when()
                .put("/employees/update/" + empId)
                .then()
                .body("id", Matchers.any(Integer.class))
                .body("name", Matchers.is("abc"))
                .body("salary", Matchers.is("700000"));

    }

    
}
