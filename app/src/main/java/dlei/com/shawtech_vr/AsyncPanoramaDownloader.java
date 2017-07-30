package dlei.com.shawtech_vr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncPanoramaDownloader extends AsyncTask<String, Void, Bitmap> {

        private VrPanoramaView vrPanoramaView;
        private String url;
        private float density;

        public AsyncPanoramaDownloader(VrPanoramaView vrPanoramaView, String url, float density) {
            this.vrPanoramaView = vrPanoramaView;
            this.url = url;
            this.density = density;
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

                // Tint bitmap.
                Paint paint = new Paint();
                int colourInt = Color.argb(255, 255, 190, 190);
                Log.i("Tint colour", "" + colourInt);

                // paint.setColorFilter(new PorterDuffColorFilter(colourInt, PorterDuff.Mode.SRC_IN));
                Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()); // Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapResult);
                paint.setColorFilter(new LightingColorFilter(colourInt,0));
                canvas.drawBitmap(bitmap, 0, 0, paint);
                Log.e("onPostExecute: ", "Bitmap Coloured!!");

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                // text color - #3D3D3D
                paintText.setColor(Color.rgb(250, 250, 250));
                // text size in pixels

                String text = "10% Probability";

                Log.e("onPostExecute: ", "Bitmap Adding Text!!");

                paintText.setTextSize((int) (24 * density));
                // text shadow
                paintText.setShadowLayer(1f, 0f, 1f, Color.BLACK);

                // draw text to the Canvas center
                Rect bounds = new Rect();
                paintText.getTextBounds(text, 0, text.length(), bounds);
                int x = (bitmapResult.getWidth() - bounds.width())/2;
                int y = (bitmapResult.getHeight() + bounds.height())/2;

                canvas.drawText(text, x, y, paintText);

                Log.e("onPostExecute: ", "Bitmap ADDED %!!!");

                VrPanoramaView.Options options = new VrPanoramaView.Options();
                vrPanoramaView.loadImageFromBitmap(bitmapResult, options);
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
