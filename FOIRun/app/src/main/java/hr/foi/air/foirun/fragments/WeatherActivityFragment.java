package hr.foi.air.foirun.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.air.foirun.R;
import hr.foi.air.owf.JSONWeatherParser;
import hr.foi.air.owf.client.WeatherHttpClient;
import hr.foi.air.owf.model.Weather;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherActivityFragment extends Fragment {

    private AsyncTask<String, Void, Weather> weather;

    @BindView(R.id.cityText)
    TextView cityText;

    @BindView(R.id.condDescr)
    TextView condDescr;

    @BindView(R.id.temp)
    TextView temp;

    @BindView(R.id.hum)
    TextView hum;

    @BindView(R.id.press)
    TextView press;

    @BindView(R.id.windSpeed)
    TextView windSpeed;

    @BindView(R.id.windDeg)
    TextView windDeg;

    @BindView(R.id.condIcon)
    ImageView imgView;

    public WeatherActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_forecast, container, false);

        ButterKnife.bind(this, view);

        // Inflate the layout for this fragment
        return view;
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                if(img != null){
                    imgView.setImageBitmap(Bitmap.createScaledBitmap(img, 200, 200, false));
                }
            }

            if(weather.location != null) {
                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                windDeg.setText("" + weather.wind.getDeg() + "°");
            }
        }
    }

    public void executeTask(String queryString) {
        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(queryString);
    }
}
