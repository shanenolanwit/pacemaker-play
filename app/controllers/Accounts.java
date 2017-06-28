package controllers;

import java.util.List;

import javax.inject.Inject;

import models.Activity;
import models.Location;
import models.User;
import parsers.GoogleParser;
import play.*;
import play.mvc.*;

import views.html.*;

import play.data.FormFactory; 
import play.data.Form;


public class Accounts extends Controller
{  
 
  private static Form<User> userForm;
  private static Form<User> loginForm;

  @Inject
  public Accounts(FormFactory formFactory) {
     this.userForm  = formFactory.form(User.class);
     this.loginForm = formFactory.form(User.class);
  }
 
  public Result index()
  {
	  String user = session("email");
	    if(user != null) {
	    	return redirect("/dashboard");
	    } else {
	    	return ok(welcome_main.render());
	    }
    
  }

  public Result signup()
  {
    return ok(accounts_signup.render());
  }
  
  public Result login()
  {
    return ok(accounts_login.render());
  }
  
  public Result logout()
  {
    session().clear();
    return ok(welcome_main.render());
  }

  public Result register()
  {
	Form<User> boundForm = userForm.bindFromRequest();    
	if(userForm.hasErrors()) 
    {
      return badRequest(accounts_login.render());
    }
    else
    {
      User user = boundForm.get();
      Logger.info ("User = " + user.toString());
      user.save();
      session("email", boundForm.get().email);
      return redirect(routes.Dashboard.index());
    }
  }

  public Result authenticate() 
  {
	Form<User> boundForm = loginForm.bindFromRequest();    
	
	if(loginForm.hasErrors()) 
    {
      return badRequest(accounts_login.render());
    } 
    else 
    {	
       session("email", boundForm.get().email);
       return redirect(routes.Dashboard.index());
    }
  }
  
  
  public Result friends()
  {
	  
	  String email = session("email");
	   if(email != null && User.findByEmail(email) != null) {
		    
	    	User user = User.findByEmail(email);
	    	return ok(friend_manager.render(user));
	    } else {
	    	return ok(welcome_main.render());
	    }
    
  }
  
  public Result myAccount()
  {
	  
	  String email = session("email");
	   if(email != null && User.findByEmail(email) != null) {
		    
	    	User user = User.findByEmail(email);
	    	return ok(accounts_myaccount.render(user));
	    } else {
	    	return ok(welcome_main.render());
	    }
    
  }
  
  public Result delete(){
	  String email = session("email");
	   if(email != null && User.findByEmail(email) != null) {
		    
	    	User user = User.findByEmail(email);
	    	for(User u : user.friends){
	    		u.friends.remove(user);
	    		u.save();
	    	}
	    	user.delete();
	    	
	    	session().clear();
	    	
	    } 
	   return ok(welcome_main.render());
  }
  
  
  
  public Result update()
  {
	Form<User> boundForm = userForm.bindFromRequest();  
    User updatedUser = boundForm.get();
    String email = session("email");
    User user = null;

    if(boundForm.hasErrors()) 
    {
      return badRequest();
    }    
	if(email == null || User.findByEmail(email) == null){
		return notFound();
  	}else{
  		user = User.findByEmail(email);
  	}   	  		   
   	  		user.firstname = updatedUser.firstname;
   	  		user.lastname = updatedUser.lastname;
   	  		user.email = updatedUser.email;
   	  		user.password = updatedUser.password;
   	  		user.phaseInterval = updatedUser.phaseInterval;
   	  		user.save();
   	  	session("email", updatedUser.email);
   	  	
   	  	return redirect(routes.Dashboard.index());
   	  	
    
  }
  

}
