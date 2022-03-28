package com.example.application.rest;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;


public class Weather {

    private String main;
    private String icon;


    public Weather(String icon) {
        setIcon(icon);
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Component getIconImageComponent(){
        return new Image("http://openweathermap.org/img/wn/"+ getIcon() + "@2x.png", getIcon() + ".png");
    }
}
