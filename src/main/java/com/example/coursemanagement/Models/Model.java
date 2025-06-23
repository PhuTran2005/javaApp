package com.example.coursemanagement.Models;

import com.example.coursemanagement.Views.ViewFactory;
import com.example.coursemanagement.Utils.SessionManager;



public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    private Model() {
        this.viewFactory = new ViewFactory();
    }
    private final SessionManager sessionManager = SessionManager.getInstance();

    public static synchronized Model getInstance() {

        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
    public SessionManager getSessionManager() {
        return sessionManager;
    }

}
