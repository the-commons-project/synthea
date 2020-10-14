package org.mitre.synthea.client;

import com.google.gson.JsonObject;
import org.hl7.fhir.r4.model.Bundle;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IHapiFhirService {
    @Headers("Content-Type: application/fhir+json")
    @POST("/fhir")
    Call<JsonObject> addBundle(@Body JsonObject bundle);
}
