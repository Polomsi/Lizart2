package com.franciscopolov.lizart.lizart2;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Created by Polo on 15/06/2015.
 */
public class IniciaParse extends Application {
    @Override
    public void onCreate(){
        Parse.initialize(this, "oDrnnS7ZGcsidO7FAhVNKmOEq8VYsviLE3JTDeWD", "XvbJZW4gCBrnjOf3uERwgi4FRHpuDKWgKVz3bAI9");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        PushService.setDefaultPushCallback(this, Lizart.class);
    }
}
