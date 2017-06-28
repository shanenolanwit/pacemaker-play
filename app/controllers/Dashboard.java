package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Activity;
import models.Location;
import models.User;
import parsers.GoogleParser;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Dashboard extends Controller
{
	
  public Result index()
  {
	 
	  String email = session("email");
	   if(email != null && User.findByEmail(email) != null) {
		    
	    	User user = User.findByEmail(email);
	    	
	    	
	    	return ok(dashboard_main.render(user));
	    } else {
	    	return ok(welcome_main.render());
	    }
   
  }
  
  
 
  
 
}
