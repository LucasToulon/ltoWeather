package com.example.application.views.home;

import com.example.application.rest.Main;
import com.example.application.rest.Location;
import com.example.application.rest.Weather;
import com.example.application.rest.WeatherGetter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Home")
@CssImport(value = "./styles/views/home/home-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class HomeView extends Div implements AfterNavigationObserver {

    static Logger logger = LoggerFactory.getLogger(HomeView.class);
    static WeatherGetter weatherGetter = new WeatherGetter();
    static Location coord = new Location();
    public HomeView() {
        setId("home-view");
        addClassName("home-view");
        setSizeFull();
        VerticalLayout basicLayout = new VerticalLayout();
        H1 header = new H1("WetterApp");
        H2 subheader = new H2("Fragen Sie das Wetter für eine bestimmte PLZ oder Stadt ab");
        basicLayout.add(header, subheader);
        add(basicLayout);
        //We build the city chooser later
        askZipCodeOrCity();
    }


    private void askZipCodeOrCity() {
        VerticalLayout queryLayoutContainer = new VerticalLayout();
        FormLayout queryLayout = new FormLayout();
        TextField zipCodeOrCity = new TextField("Postleitzahl oder Stadt:");
        queryLayout.add(zipCodeOrCity);
        Button lookupWeatherButton = new Button("Wetter abrufen!");
        queryLayoutContainer.add(queryLayout, lookupWeatherButton);
        add(queryLayoutContainer);
        VerticalLayout weatherResultLayout = new VerticalLayout();
        lookupWeatherButton.addClickListener(click -> {
            //Altes Wetter weg
            remove(weatherResultLayout);
            weatherResultLayout.removeAll();
            //Neues Wetter abrufen
            weatherResultLayout.add(lookupWeatherForData(zipCodeOrCity.getValue()));
            add(weatherResultLayout);

        });
    }

    //Interpretiert die eingegebenen Daten, aktualisiert das Wetter und zeigt die neuen Daten an.
    private Component lookupWeatherForData(String zipCodeOrCity) {
        //Zuerst prüfen, ob PLZ oder Stadt befüllt, entsprechend rufen wir eine andere URL ab
        if(StringUtils.isBlank(zipCodeOrCity.trim())){
            //beides fehlt
            Notification.show("Bitte ausfüllen");
        }else if(StringUtils.isNumeric(zipCodeOrCity)){
            Notification.show("Suche Wetter für " + zipCodeOrCity);
            //Den Zip Converter abrufen und damit eine Wetter Abfrage machen
            makeConverterServiceZip(zipCodeOrCity);
            updateWeather();
        }else if(StringUtils.isAlpha(zipCodeOrCity)){
            Notification.show("Suche Wetter für " + zipCodeOrCity);
            //Den City Converter abrufen und damit eine Wetter Abfrage machen
            makeConverterServiceCity(zipCodeOrCity);
            updateWeather();
        }else{
            Notification.show("Bitte nur die PLZ oder die Stadt eintragen");
            //beides befüllt...
        }

        //Wir holen uns die aktualisierten Daten
        VerticalLayout weatherResultInnerLayout = new VerticalLayout();
        Paragraph p = new Paragraph("Wetter für: " + coord.getName());
        weatherResultInnerLayout.add(p);
        Paragraph p1 = new Paragraph("Temperatur: " + weatherGetter.getMain().getTemp() + "°C");
        Paragraph p2 = new Paragraph("Gefühlt wie: " + weatherGetter.getMain().getFeelsLike() + "°C");
        weatherResultInnerLayout.add(p1, p2, weatherGetter.getWeather().get(0).getIconImageComponent());
        return weatherResultInnerLayout;
    }

    //Der tatsächliche Aufruf des Wetters mit lat und lon
    private void updateWeather() {
        if(coord.getLat() == null || coord.getLon() == null){
            Notification.show("Keine Stadt gefunden");
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat=" +  coord.getLat().toString() + "&lon=" + coord.getLon().toString() + "&appid=30e793cadb8bb8eea299ac7b29d73f50&units=metric")).build();
        try{
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(HomeView::updateWeatherMainWithJSONWeather).join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Interpretiert das JSON-Ergebnis in die statischen Variablen
    private static void updateWeatherMainWithJSONWeather(String responseBody) {
        /*
        {
          "coord": {
            "lon": -122.08,
            "lat": 37.39
          },
          "weather": [
            {
              "id": 800,
              "main": "Clear",
              "description": "clear sky",
              "icon": "01d"
            }
          ],
          "base": "stations",
          "main": {
            "temp": 282.55,
            "feels_like": 281.86,
            "temp_min": 280.37,
            "temp_max": 284.26,
            "pressure": 1023,
            "humidity": 100
          },
          "visibility": 10000,
          "wind": {
            "speed": 1.5,
            "deg": 350
          },
          "clouds": {
            "all": 1
          },
          "dt": 1560350645,
          "sys": {
            "type": 1,
            "id": 5122,
            "message": 0.0139,
            "country": "US",
            "sunrise": 1560343627,
            "sunset": 1560396563
          },
          "timezone": -25200,
          "id": 420006353,
          "name": "Mountain View",
          "cod": 200
          }*/
        JSONArray coords = new JSONArray("[" + responseBody + "]");
        for(int i=0; i<1; i++){
            JSONObject coordJSON = coords.getJSONObject(i);
            weatherGetter.setMain(new Main(coordJSON.getJSONObject("main").getDouble("temp"), coordJSON.getJSONObject("main").getDouble("feels_like")));
            JSONArray weathers = coordJSON.getJSONArray("weather");
            List<Weather> weatherList = new ArrayList<Weather>();
            weatherList.add(new Weather(weathers.getJSONObject(0).getString("icon")));
            weatherGetter.setWeather(weatherList);
        }
    }

    //Holt sich die Koordinaten für einen Stadtnamen
    private void makeConverterServiceCity(String city) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.openweathermap.org/geo/1.0/direct?q=" + city + ",de&limit=1&appid=30e793cadb8bb8eea299ac7b29d73f50")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(HomeView::updateCoordWithJSONCity).join();
    }

    //Interpretiert die gefundenen Städte.
    private static void updateCoordWithJSONCity(String responseBody){
        /*{
            "name": "London",
            "local_names": {
              "ascii": "London",
              "en": "London",
              "feature_name": "London"
            },
            "lat": 51.5085,
            "lon": -0.1257,
            "country": "DE"
          }*/
        JSONArray coords = new JSONArray(responseBody);
        for(int i=0; i<1; i++){
            JSONObject coordJSON = coords.getJSONObject(i);
            coord.setLat(coordJSON.getDouble("lat"));
            coord.setLon(coordJSON.getDouble("lon"));
            coord.setName(coordJSON.getString("name"));
        }
    }

    //Holt sich die Koordinaten für eine PLZ
    private void makeConverterServiceZip(String zip) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.openweathermap.org/geo/1.0/zip?zip=" + zip + ",de&appid=30e793cadb8bb8eea299ac7b29d73f50")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(HomeView::updateCoordWithJSONZip).join();
    }

    //Interpretiert die gefundene PLZ.
    private static void updateCoordWithJSONZip(String responseBody){
        /*{
          "zip": "90210",
          "name": "Stuttgart",
          "lat": 34.0901,
          "lon": -118.4065,
          "country": "DE"
        }*/
        JSONArray coords = new JSONArray("[" + responseBody + "]");
        for(int i=0; i<1; i++){
            JSONObject coordJSON = coords.getJSONObject(i);
            coord.setLat(coordJSON.getDouble("lat"));
            coord.setLon(coordJSON.getDouble("lon"));
            coord.setName(coordJSON.getString("name"));
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
    }
}