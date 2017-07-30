package dlei.com.shawtech_vr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncPanoramaDownloader extends AsyncTask<String, Void, Bitmap> {

        private VrPanoramaView vrPanoramaView;
        private String url;

        public AsyncPanoramaDownloader(VrPanoramaView vrPanoramaView, String url) {
            this.vrPanoramaView = vrPanoramaView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (bitmap != null) {
                Log.i("onPostExecute: ", "Setting bitmap");

                VrPanoramaView.Options options = new VrPanoramaView.Options();
                vrPanoramaView.loadImageFromBitmap(bitmap, options);
                Log.e("onPostExecute: ", "Bitmap set!!!!!");

            } else {
                // TODO: Handle.
                Log.e("onPostExecute: ", "Error: Bitmap is null");
            }
        }

        private Bitmap downloadBitmap() {
            String url = this.url;
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode != 200) {
                    Log.e("downloadBitamp: ", "Status code is " + statusCode);
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                Log.e("inputStream: ", "done");
                if (inputStream != null) {
                    Log.e("inputStream", "Should not be null: " + inputStream);

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    return bitmap;
                }
                Log.e("bitmap is null?", "NULL");
                Log.w("Downloading image from ",  url);

            } catch (Exception e) {
                Log.d("URLCONNECTIONERROR", e.toString());
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();

                }
            }
            return null;
        }
}
