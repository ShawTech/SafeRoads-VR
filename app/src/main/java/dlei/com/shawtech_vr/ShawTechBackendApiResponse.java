package dlei.com.shawtech_vr;

public class ShawTechBackendApiResponse {
    private String path;
    private String error;
    private String created;

    public String getCreated() {
        return created;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return "path: " + path + ", error: " + error + ", created: " + created;
    }
}

