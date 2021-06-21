package com.example.scoutingplatform;

import com.google.gson.annotations.SerializedName;

class ApiResp {

    public ApiResp(Integer status, Integer id, String key, String error, Integer code, String source, String message) {
        this.status = status;
        this.id = id;
        this.key = key;
        this.error = error;
        this.code = code;
        this.source = source;
        this.message = message;
    }

    @SerializedName("status")
    private Integer status;

    @SerializedName("id")
    private Integer id;

    @SerializedName("key")
    private String key;

    @SerializedName("error")
    private String error;

    @SerializedName("code")
    private Integer code;

    @SerializedName("source")
    private String source;

    @SerializedName("message")
    private String message;

    public String getError() {
        return error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

}
