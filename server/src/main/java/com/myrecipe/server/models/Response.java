package com.myrecipe.server.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Response {
    private int code;
    private String message;
    private JsonObject data = Json.createObjectBuilder().build();

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public JsonObject getData() {
        return data;
    }
    public void setData(JsonObject data) {
        this.data = data;
    }
    public JsonObject toJson() {
        return Json.createObjectBuilder().add("code", code).add("message", message).add("data", data).build();
    }
}
