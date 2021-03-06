package hr.foi.air.owf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hr.foi.air.owf.model.*;

public class JSONWeatherParser {

    public static final String LATLON_PART = "?lat=%f&lon=%f";
    public static final String LOCATION_PART = "?q=%s";
    public static final String API_KEY_PART = "&appid=%s";
    public static final String LANGUAGE_PART = "&lang=%s";

    public static Weather getWeather(String data) throws JSONException  {
        Weather weather = new Weather();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        // We start extracting the info
        Location loc = new Location();

        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        weather.location = loc;

        // We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("weather");

        // We use only the first value
        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weather.currentCondition.setDescr(getString("description", JSONWeather));
        weather.currentCondition.setCondition(getString("main", JSONWeather));
        weather.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        weather.currentCondition.setHumidity(getInt("humidity", mainObj));
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        weather.wind.setSpeed(getFloat("speed", wObj));
        weather.wind.setDeg(getFloat("deg", wObj));

        // Clouds
        JSONObject cObj = getObject("clouds", jObj);
        weather.clouds.setPerc(getInt("all", cObj));

        // We download the icon to show


        return weather;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        if(jObj.has(tagName)){
            return jObj.getJSONObject(tagName);
        }
        return jObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        if(jObj.has(tagName)){
            return jObj.getString(tagName);
        }
        return "";
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        if(jObj.has(tagName)){
            return (float)jObj.getDouble(tagName);
        }
        return 0;
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        if(jObj.has(tagName)){
            return jObj.getInt(tagName);
        }
        return 0;
    }
}
