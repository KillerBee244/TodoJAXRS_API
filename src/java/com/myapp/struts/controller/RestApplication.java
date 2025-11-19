package com.myapp.struts.controller;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")  // → base path: /api
public class RestApplication extends Application {
    // Không cần code gì thêm
}
