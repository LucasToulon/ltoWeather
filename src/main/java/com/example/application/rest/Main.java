
package com.example.application.rest;

import java.util.HashMap;
import java.util.Map;


public class Main {

    private Double temp;
    private Double feelsLike;

    public Main(Double temp, Double feelsLike){
        setTemp(temp);
        setFeelsLike(feelsLike);
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }


}
