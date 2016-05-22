package com.example.sick.amarskenderovic_pset5;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherActivity extends AppCompatActivity {

    // Declaring variables
    EditText etCityName;
    Button btnFindWeather;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        etCityName = (EditText)findViewById(R.id.cityname);
        btnFindWeather = (Button)findViewById(R.id.cityweather);
        textViewResult = (TextView)findViewById(R.id.result);

        // Set click listener
        btnFindWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OpenWeatherMapTask(
                        etCityName.getText().toString(),
                        textViewResult).execute();
            }
        });
    }

    // Class for retrieving the API from Openweather.org
    private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

        String cityName;
        TextView tvResult;

        // Personal key to use the api
        String apiKey = "802176cecce2aa6d0088b274e1fdb20d";

        // URL used to get the data
        String basicURL = "http://api.openweathermap.org/data/2.5/weather?q=";

        // Final URL used by combining the id, key (also change the degrees to Celsius)
        String finalURL = "&appid=" + apiKey + "&units=metric";

        // Open the weathermap of the cityname and show it in the textview
        OpenWeatherMapTask(String cityName, TextView tvResult){
            this.cityName = cityName;
            this.tvResult = tvResult;
        }


        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String queryReturn;
            String query = "";
            try {
                query = basicURL + URLEncoder.encode(cityName, "UTF-8") + finalURL;
                queryReturn = sendQuery(query);
                result += ParseJSON(queryReturn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                queryReturn = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                queryReturn = e.getMessage();
            }
            return result;
        }

        // On execute show the results in the textview
        @Override
        protected void onPostExecute(String s) {
            tvResult.setText(s);
        }

        // Overthrow the IO exception and continue with making a connection
        private String sendQuery(String query) throws IOException {
            String result = "";
            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);

                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
            }

            return result;
        }

        private String ParseJSON(String json){
            String jsonResult = "";

            try {
                JSONObject JsonObject = new JSONObject(json);
                String cod = jsonHelperGetString(JsonObject, "cod");

                if(cod != null){
                    if(cod.equals("200")){

                        jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
                        JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                        if(sys != null){
                            jsonResult += jsonHelperGetString(sys, "country") + "\n";
                        }
                        jsonResult += "\n";

                        // Get the temp & humidity from the json
                        JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                        if(main != null){
                            jsonResult += "Temperature (in Celsius): " + jsonHelperGetString (main, "temp") + "\n";
                            jsonResult += "Humidity: " + jsonHelperGetString(main, "humidity") + "\n";
                            jsonResult += "\n";
                        }

                        // Get the weather info
                        JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                        if(weather != null){
                            for(int i=0; i<weather.length(); i++){
                                JSONObject thisWeather = weather.getJSONObject(i);
                                jsonResult += "Weather info" + ":\n";
                                jsonResult += jsonHelperGetString(thisWeather, "main") + "\n";
                                jsonResult += "\n";
                            }
                        }

                        // Get the wind, speed and the degree where the wind is coming from, from the JSONobject
                        JSONObject wind = jsonHelperGetJSONObject(JsonObject, "wind");
                        if(wind != null){
                            jsonResult += "Wind:\n";
                            jsonResult += "Speed: " + jsonHelperGetString(wind, "speed") + "\n";
                            jsonResult += "Deg: " + jsonHelperGetString(wind, "deg") + "\n";
                            jsonResult += "\n";
                        }

                        // Get the coordinates
                        JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
                        if(coord != null){
                            String lon = jsonHelperGetString(coord, "lon");
                            String lat = jsonHelperGetString(coord, "lat");
                            jsonResult += "Lon: " + lon + "\n";
                            jsonResult += "Lat: " + lat + "\n";
                        }
                        jsonResult += "\n";

                    // Else statement to reply an error if it goes wrong
                    }else if(cod.equals("404")){
                        String message = jsonHelperGetString(JsonObject, "message");
                        jsonResult += "cod 404: " + message;
                    }
                }else{
                    jsonResult += "cod == null\n";
                }

            } catch (JSONException e) {
                e.printStackTrace();
                jsonResult += e.getMessage();
            }
            return jsonResult;
        }

        // Get JSON String
        private String jsonHelperGetString(JSONObject obj, String k){
            String v = null;
            try {
                v = obj.getString(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return v;
        }

        // JSONObject where I want to get the JSONObject
        private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
            JSONObject o = null;
            try {
                o = obj.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return o;
        }

        // JSONArray with multiple objects where I want to get the JSONArray
        private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
            JSONArray a = null;
            try {
                a = obj.getJSONArray(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return a;
        }
    }
}