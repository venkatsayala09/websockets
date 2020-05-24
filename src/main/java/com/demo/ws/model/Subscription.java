package com.demo.ws.model;

public class Subscription {

    private String content;

    public Subscription() {
    }

    public Subscription(String name) {
        this.content = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
