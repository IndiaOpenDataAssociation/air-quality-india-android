package com.idn0phl3108ed43d22s30.pojo;

import com.idn0phl3108ed43d22s30.etc.ApiKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jimish on 4/7/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
    private static final String TAG = "payload";

    @JsonProperty(ApiKeys.PAYLOAD_D)
    private PayloadData data;

    public Payload(PayloadData data){
        this.data = data;
    }

    public Payload(){}

    public PayloadData getData() {
        return data;
    }

    public void setData(PayloadData data) {
        this.data = data;
    }
}
