package edu.ub.pis2324.projecte.presentation;

import android.app.Application;

public class MyApplication extends Application {
    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer();
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}