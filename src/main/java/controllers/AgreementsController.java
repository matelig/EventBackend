package controllers;

import com.google.gson.Gson;
import model.UserAgreementResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Path("/agreements")
public class AgreementsController {

    @GET
    @Produces("application/json")
    public Response getUserAgreement() {
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/agreement.txt")));
            String line = br.readLine();
            while (line != null) {
                fileContent.append(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            //todo change to ApiException
            return Response.status(Response.Status.NOT_FOUND).entity(new Gson().toJson("File not found")).build();
        }
        UserAgreementResponse response = new UserAgreementResponse(fileContent.toString());
        return Response.ok(new Gson().toJson(response)).build();
    }
}
