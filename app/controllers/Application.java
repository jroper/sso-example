package controllers;

import controllers.security.SamlAuthenticated;
import play.*;
import play.mvc.*;

import views.html.*;

@SamlAuthenticated
public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render("Hello " + request().username()));
    }
  
}
