package org.mitre.synthea.export.cp;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.mitre.synthea.client.IHapiFhirService;
import org.mitre.synthea.client.IPersonService;
import org.mitre.synthea.client.TCPPerson;
import org.mitre.synthea.helpers.Config;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PersonFetcher implements IPersonFetcher {
    private IPersonService personService;

    public PersonFetcher() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.get("exporter.person.api"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        this.personService = retrofit.create(IPersonService.class);
    }

    public List<TCPPerson> getTcpPersons() throws IOException {
        Call<List<TCPPerson>> res = personService.getPersons();
        Response<List<TCPPerson>> response = res.execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Error getting persons");
        }
        return response.body();

    }
}
