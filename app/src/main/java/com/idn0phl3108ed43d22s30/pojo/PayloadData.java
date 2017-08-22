package com.idn0phl3108ed43d22s30.pojo;

import android.content.Intent;
import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jimish on 4/7/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadData {
    private static final String TAG = "PayloadData";

    @JsonProperty(ApiKeys.D_G1)
    private float g1;

    @JsonProperty(ApiKeys.D_G2)
    private float g2;

    @JsonProperty(ApiKeys.D_G3)
    private float g3;

    @JsonProperty(ApiKeys.D_G4)
    private float g4;

    @JsonProperty(ApiKeys.D_G5)
    private float g5;

    @JsonProperty(ApiKeys.D_G6)
    private float g6;

    @JsonProperty(ApiKeys.D_G7)
    private float g7;

    @JsonProperty(ApiKeys.D_G8)
    private float g8;

    @JsonProperty(ApiKeys.D_G9)
    private float g9;

    @JsonProperty(ApiKeys.D_P1)
    private float p1;

    @JsonProperty(ApiKeys.D_P2)
    private float p2;

    @JsonProperty(ApiKeys.D_P3)
    private float p3;

    @JsonProperty(ApiKeys.D_TEMP)
    private int temp;

    @JsonProperty(ApiKeys.D_HUM)
    private int humidity;

    @JsonProperty(ApiKeys.D_T)
    private String time;

    @JsonProperty(ApiKeys.D_CPCB_P1)
    private float cpcb_p1;

    @JsonProperty(ApiKeys.D_CBCB_P2)
    private float cpcb_p2;

    @JsonProperty(ApiKeys.D_CO)
    private float co;

    @JsonProperty(ApiKeys.D_NO2)
    private float no2;

    @JsonProperty(ApiKeys.D_SO2)
    private float so2;

    @JsonProperty(ApiKeys.D_O3)
    private float o3;


    public PayloadData(float g2, float g1, float g3, float g4, float g5, float g6, float g7, float g8, float g9, float p1, float p2, int temp, int humidity, String time) {
        this.g2 = g2;
        this.g1 = g1;
        this.g3 = g3;
        this.g4 = g4;
        this.g5 = g5;
        this.g6 = g6;
        this.g7 = g7;
        this.g8 = g8;
        this.g9 = g9;
        this.p1 = p1;
        this.p2 = p2;
        this.temp = temp;
        this.humidity = humidity;
        this.time = time;
    }

    public PayloadData(float g1, float g2, float g3, float g4, float g5, float g6, float g7, float g8, float g9, float p1, float p2, int temp, int humidity, String time, float cpcb_p1, float cpcb_p2, float co, float no2, float so2, float o3) {
        this.g1 = g1;
        this.g2 = g2;
        this.g3 = g3;
        this.g4 = g4;
        this.g5 = g5;
        this.g6 = g6;
        this.g7 = g7;
        this.g8 = g8;
        this.g9 = g9;
        this.p1 = p1;
        this.p2 = p2;
        this.temp = temp;
        this.humidity = humidity;
        this.time = time;
        this.cpcb_p1 = cpcb_p1;
        this.cpcb_p2 = cpcb_p2;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
        this.o3 = o3;
    }

    public float getCpcb_p1() {
        return cpcb_p1;
    }

    public void setCpcb_p1(float cpcb_p1) {
        this.cpcb_p1 = cpcb_p1;
    }

    public float getCpcb_p2() {
        return cpcb_p2;
    }

    public void setCpcb_p2(float cpcb_p2) {
        this.cpcb_p2 = cpcb_p2;
    }

    public float getCo() {
        return co;
    }

    public void setCo(float co) {
        this.co = co;
    }

    public float getNo2() {
        return no2;
    }

    public void setNo2(float no2) {
        this.no2 = no2;
    }

    public float getSo2() {
        return so2;
    }

    public void setSo2(float so2) {
        this.so2 = so2;
    }

    public float getO3() {
        return o3;
    }

    public void setO3(float o3) {
        this.o3 = o3;
    }

    public PayloadData() {
    }

    public float getG1() {
        return g1;
    }

    public void setG1(float g1) {
        this.g1 = g1;
    }

    public float getG2() {
        return g2;
    }

    public void setG2(float g2) {
        this.g2 = g2;
    }

    public float getG3() {
        return g3;
    }

    public void setG3(float g3) {
        this.g3 = g3;
    }

    public float getG4() {
        return g4;
    }

    public void setG4(float g4) {
        this.g4 = g4;
    }

    public float getG5() {
        return g5;
    }

    public void setG5(float g5) {
        this.g5 = g5;
    }

    public float getG6() {
        return g6;
    }

    public void setG6(float g6) {
        this.g6 = g6;
    }

    public float getG7() {
        return g7;
    }

    public void setG7(float g7) {
        this.g7 = g7;
    }

    public float getG8() {
        return g8;
    }

    public void setG8(float g8) {
        this.g8 = g8;
    }

    public float getG9() {
        return g9;
    }

    public void setG9(float g9) {
        this.g9 = g9;
    }

    public float getP1() {
        return p1;
    }

    public void setP1(float p1) {
        this.p1 = p1;
    }

    public float getP2() {
        return p2;
    }

    public void setP2(float p2) {
        this.p2 = p2;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    //
    public int getP3() {
        int a = Math.round(p3);

        return a;
    }

    public void setP3(float p3) {
        this.p3 = p3;
    }
}
