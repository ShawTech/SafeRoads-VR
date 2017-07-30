package dlei.com.shawtech_vr;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.google.vr.sdk.
//    <com.google.vr.sdk.widgets.pano.VrPanoramaView

public class StreetViewVirtualActivity extends AppCompatActivity {
    VrPanoramaView mVrPanoramaView;
    public static final String BACKEND_API_ENDPOINT = "https://saferoads-vr-backend.herokuapp.com/";
    public static final String DEFAULT_PANO_PATH = "images/DEFAULT.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view_virtual);
        mVrPanoramaView = (VrPanoramaView) findViewById(R.id.vrPanoramaView);

        Intent i = getIntent();
        String needRemoteImage = i.getStringExtra("remote_image");
        if (needRemoteImage.equals("NO")) {
            loadPanoramaFromAssets("s.jpg");
        } else {
            String lat = "-37.8098879";
            String lng = "144.9696459";
            panoramaRequest(lat, lng);
        }
    }

    // TODO: Convert lat lng to strings.
    private void panoramaRequest(final String lat, final String lng) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS);
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request r = chain.request();
                HttpUrl url = r.url().newBuilder()
                        .addQueryParameter("lat", lat)
                        .addQueryParameter("lng", lng)
                        .build();
                r = r.newBuilder().url(url).build();
                return chain.proceed(r);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient.build())
                .baseUrl(BACKEND_API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ShawTechVrBackendApi api = retrofit.create(ShawTechVrBackendApi.class);

        // Make the call.
        Call<ShawTechBackendApiResponse> call = api.getPanorama();

        // Queue the call.
        call.enqueue(new Callback<ShawTechBackendApiResponse>() {
            @Override
            public void onResponse(Call<ShawTechBackendApiResponse> call, retrofit2.Response<ShawTechBackendApiResponse> response) {
                Log.e("panoramaRequest", "Success");
                if (response.code() == 200 && response.isSuccessful()) {
                    Log.i("panoramaRequest", "Success");
                    Log.e("Response json: ", response.body().toString());

                    // Download image and set bitmap.
                    String tokens[] = response.body().getPath().split("/", 2);
                    Log.i("panoramaRequest", "tokens: " + Arrays.toString(tokens));
                    String imagePath = "/" + tokens[1];
                    Log.i("panoramaRequest", "image path: " + imagePath);
                    String panoramaUrl = BACKEND_API_ENDPOINT + imagePath;
                    loadPanorama(panoramaUrl);

                } else {
                    Log.e("panoramaRequest", "Failed but got response");
                    Log.e("Response Error: ", response.message());
                    Log.e("Response json: ", response.body().toString());
                    Log.e("panoramaRequest", "Loading default");
                    loadPanorama(BACKEND_API_ENDPOINT + DEFAULT_PANO_PATH);

                }

            }

            @Override
            public void onFailure(Call<ShawTechBackendApiResponse> call, Throwable t) {
                Log.e("panoramaRequest", "Failed " + t.getMessage());
                Log.e("panoramaRequest", "Loading default");
                loadPanorama(BACKEND_API_ENDPOINT + DEFAULT_PANO_PATH);


            }
        });


    }

    // Load panorama from assets.
    private void loadPanoramaFromAssets(String name) {
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        InputStream inputStream = null;

        AssetManager assetManager = getAssets();

        try {
            inputStream = assetManager.open(name);
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            mVrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options);
            inputStream.close();
        } catch (IOException e) {
            Log.e("loadPanoramaFromAssets", "Error: " + e.getMessage() );
        }

    }

    // Load a panorama from a url.
    private void loadPanorama(String url) {
        Log.i("loadPanorama", "Starting");
        new AsyncPanoramaDownloader(mVrPanoramaView, url).execute();
    }

    @Override
    protected void onPause() {
        mVrPanoramaView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVrPanoramaView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        mVrPanoramaView.shutdown();
        super.onDestroy();
    }
}