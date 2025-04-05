package org.rococo.tests.client.gateway;

import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.model.RestPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountriesApi {

    @GET("/api/country/{id}")
    Call<CountryDTO> findById(@Path("id") UUID id);

    @GET("/api/country/code/{code}")
    Call<CountryDTO> findByCode(@Path("code") String code);

    @GET("/api/country")
    Call<RestPage<CountryDTO>> findAll(@Query("page") int page,
                                       @Query("size") int size);

}
