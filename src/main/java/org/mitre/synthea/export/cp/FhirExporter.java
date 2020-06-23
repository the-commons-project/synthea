package org.mitre.synthea.export.cp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.mitre.synthea.client.IHapiFhirService;
import org.mitre.synthea.export.FhirR4;
import org.mitre.synthea.helpers.Config;
import org.mitre.synthea.world.agents.Person;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class FhirExporter implements IExporter {
    IHapiFhirService service;

    public FhirExporter() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.get("exporter.fhirServer"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.service = retrofit.create(IHapiFhirService.class);
    }

    @Override
    public void export(Person person, long stopTime) {
        String patientName = person.attributes.get(Person.NAME).toString().replace(' ', '_') + "_"
                + person.attributes.get(Person.ID);
        try {
            String bundleJson = FhirR4.convertToFHIRJson(person, stopTime);
            Gson g = new Gson();
            Call<JsonObject> res = service.addBundle(g.fromJson(bundleJson, JsonObject.class));
            Response<JsonObject> response = res.execute();
            if(!response.isSuccessful()){
                System.out.println("Error sending bundle for patient " + patientName + "" +
                        " to FhirServer, code: " + response.code());
            }else{
                System.out.println("Person " + patientName + " successfully sent to FHIR server");
            }
        } catch (Exception e) {
            System.out.println("Error sending bundle for patient " + patientName + "" +
                            " to FhirServer");
            e.printStackTrace();
        }
    }
}
