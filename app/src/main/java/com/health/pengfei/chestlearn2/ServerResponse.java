package com.health.pengfei.chestlearn2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by csuml on 8/9/2017.
 */

public class ServerResponse {

    @SerializedName("message")
    String message;
    @SerializedName("error")
    boolean error;

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setError(Boolean error){
        this.error = error;
    }
    public Boolean getError(){
        return error;
    }
}
