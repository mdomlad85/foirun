package hr.foi.air.foirun.util;


import android.os.AsyncTask;

import org.json.JSONException;

import hr.foi.air.owf.JSONWeatherParser;
import hr.foi.air.owf.client.WeatherHttpClient;
import hr.foi.air.owf.model.Weather;

public class JSONWeatherTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        Weather weather = new Weather();
        String data = ( (new WeatherHttpClient()).getWeatherData(params[0].toString()));

        try {
            weather = JSONWeatherParser.getWeather(data);

            // Let's retrieve the icon
            weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
