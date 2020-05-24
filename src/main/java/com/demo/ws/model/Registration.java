package com.demo.ws.model;

import lombok.Data;

@Data
public class Registration {
    String name;
    String url;

    @Override
    public String toString() {
        return "Service Name: " + name + "Service URL: " + url;
    }
}
