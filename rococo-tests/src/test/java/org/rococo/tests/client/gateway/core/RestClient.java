package org.rococo.tests.client.gateway.core;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.interceptor.CustomHttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.config.Config;
import org.rococo.tests.enums.HttpStatus;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Optional;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static org.rococo.tests.enums.HttpStatus.SERVICE_UNAVAILABLE;

@ParametersAreNonnullByDefault
public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();
    protected static final String SERVICE_NAME = "rococo-gateway";
    private static final String REQUEST_TPL = "request-attachment.ftl",
            RESPONSE_TPL = "response-attachment.ftl";

    protected final Retrofit retrofit;
    private final OkHttpClient okHttpClient;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HEADERS, new Interceptor[0]);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HEADERS, new Interceptor[0]);
    }

    public RestClient(String baseUrl, HttpLoggingInterceptor.Level loggingLevel) {
        this(baseUrl, false, JacksonConverterFactory.create(), loggingLevel, new Interceptor[0]);
    }

    public RestClient(String baseUrl, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel) {
        this(baseUrl, false, converterFactory, loggingLevel, new Interceptor[0]);
    }

    public RestClient(String baseUrl, boolean followRedirect, HttpLoggingInterceptor.Level loggingLevel) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), loggingLevel, new Interceptor[0]);
    }

    public RestClient(String baseUrl, boolean followRedirect, HttpLoggingInterceptor.Level loggingLevel, @Nonnull Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), loggingLevel, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel) {
        this(baseUrl, followRedirect, converterFactory, loggingLevel, new Interceptor[0]);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel, @Nonnull Interceptor... interceptors) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        for (Interceptor interceptor : interceptors) {
            okHttpBuilder.addNetworkInterceptor(interceptor);
        }

        okHttpBuilder.addNetworkInterceptor(
                new CustomHttpLoggingInterceptor(getClass())
                        .setLevel(loggingLevel));

        okHttpBuilder.addInterceptor(
                new AllureOkHttp3()
                        .setRequestTemplate(REQUEST_TPL)
                        .setResponseTemplate(RESPONSE_TPL));


        okHttpBuilder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );

        this.okHttpClient = okHttpBuilder.build();

        this.retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .build();
    }

    @Nonnull
    @SneakyThrows
    protected static <T> UnknownError unknownException(@Nullable Response<T> response, Call<T> call, IOException ex) {
        var statusCode = response == null
                ? SERVICE_UNAVAILABLE
                : response.code();
        var method = call.request().method();
        var url = call.request().url().encodedPath();
        var responseBody = response != null && response.errorBody() != null
                ? response.errorBody().string()
                : null;
        return new UnknownError("Unknown error.%nStatus: %d,%nMethod: %s,%nUrl: %s,%nBody: %s,%nException message: %s%n"
                .formatted(statusCode, method, url, responseBody, ex.getMessage()));
    }

    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public <T> T execute(final Call<T> call) {
        try {
            var response = call.execute();
            return response.body();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public <T> Optional<T> executeWithOptional(final Call<T> call) {
        try {
            var response = call.execute();
            return (response.code() == HttpStatus.NOT_FOUND)
                    ? Optional.empty()
                    : Optional.ofNullable(response.body());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static final class EmptyClient extends RestClient {

        public EmptyClient(String baseUrl) {
            super(baseUrl);
        }

        public EmptyClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public EmptyClient(String baseUrl, HttpLoggingInterceptor.Level loggingLevel) {
            super(baseUrl, loggingLevel);
        }

        public EmptyClient(String baseUrl, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel) {
            super(baseUrl, converterFactory, loggingLevel);
        }

        public EmptyClient(String baseUrl, boolean followRedirect, HttpLoggingInterceptor.Level loggingLevel) {
            super(baseUrl, followRedirect, loggingLevel);
        }

        public EmptyClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel) {
            super(baseUrl, followRedirect, converterFactory, loggingLevel);
        }

        public EmptyClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level loggingLevel, @Nonnull Interceptor... interceptors) {
            super(baseUrl, followRedirect, converterFactory, loggingLevel, interceptors);
        }

    }

}
