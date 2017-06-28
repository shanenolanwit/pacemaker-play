package controllers;

import static parsers.JsonParser.*;

import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import java.time.LocalDateTime;
import java.util.*;

import models.*;
import parsers.GoogleParser;

public class PacemakerAPI extends Controller
{  
  //user methods
	  
  public Result users()
  {
    List<User> users = User.findAll();
    return ok(renderUser(users));
  }

  public Result user(Long id)
  {
    User user = User.findById(id);  
    if(user==null){
    	return notFound();
    } else{
    	return ok(renderUser(user));
    }
  }
  
  public Result friends(Long id)
  {
    User user = User.findById(id);  
    if(user==null){
    	return notFound();
    } else{
    	return ok(renderUser(user.friends));
    }
  }
  
  public Result leaderboard(Long id){
	  User user = User.findById(id);  
	    if(user==null){
	    	return notFound();
	    } else{
	    	List<User> users = user.friends;
	    	users.add(user);
	    	return ok(renderUser(users));
	    }
  }
  
  
  
  public Result createUser()
  {
    User user = renderUser(request().body().asJson().toString());
    user.save();
    return ok(renderUser(user));
  }
  
  public Result deleteUser(Long id)
  {
    Result result = notFound();
    User user = User.findById(id);
    if (user != null)
    {
    	
    	for(User u : user.friends){
    		u.friends.remove(user);
    		u.save();
    	}
      user.delete();
      result = ok();
    }
    return result;
  }
  
  public Result deleteAllUsers()
  {
	  for(User user : User.findAll()){
		  for(User u : user.friends){
			  u.friends.remove(user);
		  		u.save();
		  }
  		user.save();
  	}
    User.deleteAll();
    return ok();
  }
  
  public Result updateUser(Long id)
  {
    Result result = notFound();
    User user = User.findById(id);
    if (user != null)
    {
      User updatedUser = renderUser(request().body().asJson().toString());
      user.update(updatedUser);
      user.save();
      result = ok(renderUser(user));
    }
    return result;
  }
  
  public Result friend(Long id, Long friend_id){
	  Result result = notFound();
	  User user = User.findById(id);
	    User friend = User.findById(friend_id);
	    if (user != null && friend != null){
	    	user.friends.add(friend);
	    	friend.friends.add(user);
	    	user.save();
	    	friend.save();
	    	result = ok(renderUser(friend));
	    }
	    return result;
  }
  
  public Result unfriend(Long id, Long friend_id){
	  Result result = notFound();
	  User user = User.findById(id);
	  User friend = User.findById(friend_id);
	    if (user != null && friend != null){
	    	user.friends.remove(friend);
	    	friend.friends.remove(user);
	    	user.save();
	    	friend.save();
	    	result = ok(renderUser(friend));
	    }
	    return result;
  }
 
  //activity methods
  
  public Result activities (Long userId)
  {  
    User user = User.findById(userId);
    return ok(renderActivity(user.activities));
  }
   
  public Result createActivity (Long userId)
  { 
    User     user     = User.findById(userId);
    Activity activity = renderActivity(request().body().asJson().toString());  
    
    user.activities.add(activity);
    user.save();
    
    return ok(renderActivity(activity));
  }
  
  public Result activity (Long userId, Long activityId)
  {  
    User     user     = User.findById(userId);
    Activity activity = Activity.findById(activityId);
    
    if (activity == null)
    {
      return notFound();
    }
    else
    {
      return user.activities.contains(activity)? ok(renderActivity(activity)): badRequest();
    }
  }  
  
  public Result deleteActivity (Long userId, Long activityId)
  {  
    User     user     = User.findById(userId);
    Activity activity = Activity.findById(activityId);
 
    if (activity == null)
    {
      return notFound();
    }
    else
    {
      if (user.activities.contains(activity))
      {
        user.activities.remove(activity);
        activity.delete();
        user.save();
        return ok();
      }
      else
      {
        return badRequest();
      }

    }
  }  
  
  public Result updateActivity (Long userId, Long activityId)
  {
    User     user     = User.findById(userId);
    Activity activity = Activity.findById(activityId);
    
    if (activity == null)
    {
      return notFound();
    }
    else
    {
      if (user.activities.contains(activity))
      {
        Activity updatedActivity = renderActivity(request().body().asJson().toString());
        activity.distance = updatedActivity.distance;
        activity.location = updatedActivity.location;
        activity.activityType     = updatedActivity.activityType;
      
        activity.save();
        return ok(renderActivity(updatedActivity));
      }
      else
      {
        return badRequest();
      }
    }
  }  
  
 //location methods
  
  public Result route(Long activityId)
  {  
    Activity activity = Activity.findById(activityId);
    return ok(renderLocation(activity.route));
  }
   
  public Result createLocation(Long activityId)
  { 
	Activity activity = Activity.findById(activityId);
	float lat = Float.valueOf(Form.form().bindFromRequest().get("latitude"));
	float lng = Float.valueOf(Form.form().bindFromRequest().get("longitude"));
	int index = activity.route.size();
	Location l = new Location(lat,lng);
	activity.route.add(l);
	activity.location = l.getAddress();
	activity.lastUpdate = LocalDateTime.now();
	if(activity.route.size() > 1){
		activity.distance = 
				activity.distance 
				+ GoogleParser.getDistance(activity.route.get(activity.route.size()-2), l);
	}
	
    activity.save();
    
    return ok(renderLocation(l));
  }
  
  public Result location(Long activityId, Long locationId)
  {  
	  Activity activity = Activity.findById(activityId);
	  Location location = renderLocation(request().body().asJson().toString());  
    
	  if (location == null)
	  {
		  return notFound();
	  }
	  else
	  {
		  return activity.route.contains(location)? ok(renderLocation(location)): badRequest();
	  }
  }  
  
  public Result deleteLocation(Long activityId, Long locationId)
  {  
	  Activity activity = Activity.findById(activityId);
	  Location location = Location.findById(locationId);;  
 
	  if (location == null)
	  {
		  return notFound();
	  }
	  else
	  {
		  if (activity.route.contains(location))
		  {
			  activity.route.remove(location);
			  location.delete();
			  activity.save();
			  return ok();
		  }
		  else
		  {
			  return badRequest();
		  }

	  }	
  }  
  
  public Result gconvert(){
	  String latitude = Form.form().bindFromRequest().get("lat");
	  String longitude= Form.form().bindFromRequest().get("lng");
	  String location = GoogleParser.parseLatLong(latitude, longitude);
	  return ok(Json.toJson(location));
  }
  
 
  
}
