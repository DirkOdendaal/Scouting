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

    //{"error":400,"code":2002,"source":"content","message":"Unexpected end when deserializing object. Path 'Id', line 1, position 291."}


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    public void setStatus(Integer status) {
        this.status = status;
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

    public void setKey(String key) {
        this.key = key;
    }


}
