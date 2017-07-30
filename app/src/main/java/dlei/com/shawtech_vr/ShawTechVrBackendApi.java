package dlei.com.shawtech_vr;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ShawTechVrBackendApi {

    @GET("/streetview/panorama")
    Call<ShawTechBackendApiResponse> getPanorama();
}
