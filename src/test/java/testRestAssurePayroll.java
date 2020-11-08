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

    @Test
    public void ListEmployee() {
        Response employeeList = getEmployeeList();
        System.out.println("string is " + employeeList.asString());
    }

    @Test
    public void Check_PostEmp_Method() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\":\"xyz\",\"salary\":\"8000\"}")
                .when().post("/employees/create");
        Assert.assertEquals(201, response.getStatusCode());

    }


    @Test
    public void CheckMultiple_Post_Method_Threads() {
        String[] name = {"Abhinav", "Sumit", "Abhishek"};
        String[] salary = {"1000", "2000", "3000"};

        for (int i = 0; i < 3; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name[i]);
            map.put("salary", salary[i]);
            Runnable task = () -> {
                RestAssured.given().contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(map)
                        .when().post("/employees/create");
            };
            Thread t = new Thread(task);
            t.start();
            try {
                t.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void Check_DeleteEmp_Method() {
        int id = 1;
        Response response = RestAssured.delete("/employees/delete/" + id);
        Assert.assertEquals(200, response.getStatusCode());
    }
}
