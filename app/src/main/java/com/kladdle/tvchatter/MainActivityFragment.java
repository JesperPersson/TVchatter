package com.kladdle.tvchatter;


import android.os.AsyncTask;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        return rootView;
    }

    private void getData() {
        FetchData dataTask = new FetchData();
        dataTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        getData();
    }

    public void setDataToView(){



    }

    //Function to fetch data from moviedb.org
    public class FetchData extends AsyncTask<String, String, String[]> {

        private final String LOG_TAG = FetchData.class.getSimpleName();

        private String[] getDataFromJson(String dataString)
            throws JSONException {

            final String OWM_LIST = "results";

            JSONObject dataJSON = new JSONObject(dataString);
            JSONArray tvListArray = dataJSON.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[tvListArray.length()];
            for(int i = 0; i < tvListArray.length(); i++){
                resultStrs[i] = tvListArray.getJSONObject(i).getString("poster_path");
            }

        return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/tv/popular?sort_by=popularity.desc&api_key=687862466a5ad0d4415185b54b44b0d5");
                Log.v(LOG_TAG, "Built URI " +url);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dataString = buffer.toString();
                //Log.v("dataString",dataString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getDataFromJson(dataString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }





        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                Log.v("data", result[0]);


                for(int i = 0; i < result.length; i++) {


                    String uri = "http://image.tmdb.org/t/p/w185/" + result[i];

                    push(posterImages,uri);
                }

                //setDataToView();
            }
            else{
                Log.v("FEJL","fest");
            }
        }


    }
    public static String[] posterImages = {};

    private static String[] push(String[] array, String push) {
        String[] longer = new String[array.length + 1];
        System.arraycopy(array, 0, longer, 0, array.length);
        longer[array.length] = push;
        return longer;
    }
}
