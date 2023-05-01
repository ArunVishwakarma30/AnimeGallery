package com.example.test2.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.test2.Model.JikanApiResponse;
import com.example.test2.Model.JikanGenreResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {

    // 1.
    Context context;
    Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl("https://api.jikan.moe/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    // Here managing the API calls
    public void getAnimeHeadlines(OnFetchDataListener listener, int page) {
        CallAnimeApi callAnimeApi = retrofit.create(CallAnimeApi.class);
        Call<JikanApiResponse> call = callAnimeApi.callHeadlines(page);

        try {
            call.enqueue(new Callback<JikanApiResponse>() {
                @Override
                public void onResponse(Call<JikanApiResponse> call, Response<JikanApiResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }

                    listener.onFetchData(response.body().getData(), response.message());
                    Log.d("TAGOD", "onResponse: " + response.body());
                }

                @Override
                public void onFailure(Call<JikanApiResponse> call, Throwable t) {
                    listener.onError("Request Failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSeasonalAnime(OnFetchDataListener listener, int page) {
        CallAnimeApi callAnimeApi = retrofit.create(CallAnimeApi.class);
        Call<JikanApiResponse> call = callAnimeApi.CallSeasonalAnime(page);

        try {
            call.enqueue(new Callback<JikanApiResponse>() {
                @Override
                public void onResponse(Call<JikanApiResponse> call, Response<JikanApiResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }

                    listener.onFetchData(response.body().getData(), response.message());
                    Log.d("TAGOD", "onResponse: " + response.body());
                }

                @Override
                public void onFailure(Call<JikanApiResponse> call, Throwable t) {
                    listener.onError("Request Failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAnimeByStatus(OnFetchDataListener listener, int page, String status, int gen, String search) {
        CallAnimeApi callAnimeApi = retrofit.create(CallAnimeApi.class);
        Call<JikanApiResponse> call = callAnimeApi.CallAnimeStatus(page, status, gen, search);

        try {
            call.enqueue(new Callback<JikanApiResponse>() {
                @Override
                public void onResponse(Call<JikanApiResponse> call, Response<JikanApiResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }

                    listener.onFetchData(response.body().getData(), response.message());
                    Log.d("TAGOD", "onResponse: " + response.body());
                }

                @Override
                public void onFailure(Call<JikanApiResponse> call, Throwable t) {
                    listener.onError("Request Failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAnimeCatOnFilter(OnFetchGenreListener listener, String filter) {
        CallAnimeApi callAnimeApi = retrofit.create(CallAnimeApi.class);
        Call<JikanGenreResponse> call = callAnimeApi.getAnimeGenre(filter);

        try {
            call.enqueue(new Callback<JikanGenreResponse>() {
                @Override
                public void onResponse(Call<JikanGenreResponse> call, Response<JikanGenreResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                    listener.onFetchGenre(response.body().getData(), response.message());
                }

                @Override
                public void onFailure(Call<JikanGenreResponse> call, Throwable t) {
                    listener.OnGenreError("Request Failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2.
    public RequestManager(Context context) {
        this.context = context;
    }


    // 3. API end points
    public interface CallAnimeApi {
        @GET("top/anime")
        Call<JikanApiResponse> callHeadlines(
                @Query("page") int page

        );

        @GET("anime")
        Call<JikanApiResponse> CallAnimeStatus(
                @Query("page") int page,
                @Query("status") String status,
                @Query("genres") int gen,
                @Query("q") String search
        );

        @GET("seasons/now")
        Call<JikanApiResponse> CallSeasonalAnime(
                @Query("page") int page
        );

        @GET("genres/anime")
        Call<JikanGenreResponse> getAnimeGenre(
                @Query("filter") String filter
        );

        @GET("anime/{id}")
        Call<JikanApiResponse> CallAnimeById(
                @Path("id") int animeId
        );
    }
}
