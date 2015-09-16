package com.kladdle.tvchatter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    GridView gridview;
    ImageListAdapter imageListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void setDataToView(ArrayList mylist){
        Object[] objectList = mylist.toArray();
        String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
        gridview = (GridView) findViewById(R.id.gridview);
        imageListAdapter = new ImageListAdapter(this, stringArray);
        gridview.setAdapter(imageListAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.v("Click","click");
                Intent intent = new Intent(getApplicationContext(),DetailView.class);
                String message = "/okhLwP26UXHJ4KYGVsERQqp3129.jpg";
                intent.putExtra(intent.EXTRA_TEXT, message.toString());
                startActivity(intent);
            }
        });
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
                Log.v(LOG_TAG, "Built URI " + url);
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


        ArrayList<String> mylist = new ArrayList<String>();


        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                Log.v("data", result[0]);


                for(int i = 0; i < result.length; i++) {


                    String uri = "http://image.tmdb.org/t/p/w185/" + result[i];
                    mylist.add(uri);

                    //push(posterImages,uri);
                }
                setDataToView(mylist);
            }
            else{
                Log.v("FEJL","fest");
            }
        }


    }


    private static String[] push(String[] array, String push) {
        String[] longer = new String[array.length + 1];
        System.arraycopy(array, 0, longer, 0, array.length);
        longer[array.length] = push;
        return longer;
    }
}
