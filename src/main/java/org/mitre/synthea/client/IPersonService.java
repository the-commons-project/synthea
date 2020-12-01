package org.mitre.synthea.client;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface IPersonService {
    @Headers("Content-Type: application/json")
    @GET("/Prod/person")
    Call<List<TCPPerson>> getPersons();
}
