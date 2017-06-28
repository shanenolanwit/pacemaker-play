package controllers;

import static parsers.JsonParser.renderActivity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.Duration;

import akka.actor.ActorRef;
import akka.actor.Props;
import models.Activity;
import models.Location;
import models.User;
import parsers.GoogleParser;
import play.*;
import play.api.libs.iteratee.Concurrent;
import play.mvc.*;
import play.mvc.WebSocket;

import views.html.*;

import play.data.FormFactory;
import play.libs.Akka;
import play.data.DynamicForm;
import play.data.Form;


public class Activities extends Controller
{  
 
  private static Form<Activity> activityForm;
  private static Form<Location> locationForm;

  @Inject
  public Activities(FormFactory formFactory) {
     this.activityForm  = formFactory.form(Activity.class);
     this.locationForm = formFactory.form(Location.class);
  }
 
  public Result index()
  {
	  
	  String email = session("email");
	   if(email != null && User.findByEmail(email) != null) {
		    List<User> allUsers = User.findAll();
	    	User user = User.findByEmail(email);
	    	
	    	//List<String> locations = GoogleParser.convertLocationsForDisplay(Location.findAll());
	    	List<Location> locations = 
	    			user.activities.stream()
	    			.filter(activity -> activity.route.size() > 0)
	    			.map(a -> 
	    				a.route.get(a.route.size()-1)).collect(Collectors.toList()
	    			);
	    	return ok(activity_main.render(user,user.activities,locations));
	    } else {
	    	return ok(welcome_main.render());
	    }
    
  }
  
  public Result show(Long id)
  {
	  String email = session("email");
	    if(email != null) {
	    	User user = User.findByEmail(email);
	    	
	    	
	    	return ok(activity_show.render(user,Activity.findById(id)));
	    } else {
	    	return ok(welcome_main.render());
	    }
   
  }
  
  public Result edit(Long id)
  {
	  String email = session("email");
	  if(email != null && User.findByEmail(email) != null) {
		 
	    return ok(activity_edit.render(Activity.findById(id)));
	  } else {
	   	return ok(welcome_main.render());
	   }
   
  }
  
  public Result create()
  {
    return ok(activity_create.render());
  }  

  public Result save()
  {
	  Form<Activity> boundForm = activityForm.bindFromRequest();  
	  Activity activity = boundForm.get();
	  if(boundForm.hasErrors()) 
	  {
	    return badRequest();
	  }    
	    
	  String email = session("email");
	  if(email != null && User.findByEmail(email) != null) {	   
	   	User u = User.findByEmail(email);
	   
	   	Float lat = Float.valueOf(boundForm.data().get("latitude"));
	   	Float lng = Float.valueOf(boundForm.data().get("longitude"));
		Location location = new Location(lat,lng);
		activity.route.add(location);
		u.activities.add(activity);
		u.save();
	   	return ok(activity_show.render(u,activity));
	   } else {
	   	  return badRequest();
	   }    
  }
  
  public Result update(Long id)
  {
	Form<Activity> boundForm = activityForm.bindFromRequest();  
    Activity updatedActivity = boundForm.get();
    Activity activity = Activity.findById(id);    
  
    if(boundForm.hasErrors()) 
    {
      return badRequest();
    }    
    
    if (activity == null)
    {
      return notFound();
    }
    else
    {
    	
    	String email = session("email");
   	  	if(email != null && User.findByEmail(email) != null && User.findByEmail(email).activities.contains(activity)) {	   
   	  		activity.distance = updatedActivity.distance;
	  		activity.location = updatedActivity.location;
	  		activity.activityType     = updatedActivity.activityType.toLowerCase();	  		
	  		activity.save();
	  		return redirect(controllers.routes.Activities.show(id));
   	  		//return ok(activity_show.render(User.findByEmail(email),Activity.findById(id)));
   	  	} else {
   	  		return badRequest();
   	  	}
   	  	
    }
    
  }
  
 
  

  
}
