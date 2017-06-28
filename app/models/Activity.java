package models;

import static com.google.common.base.MoreObjects.toStringHelper;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

import flexjson.JSON;
import parsers.GoogleParser;

import javax.persistence.*;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="my_activity")
public class Activity extends Model{

  @Id
  @GeneratedValue
  public Long   id;
  
  public String activityType;
  public String location;  
  
  @Column(columnDefinition = "double precision")
  public Double distance;
  
  @Transient
  public Double kmph;
  
  @Transient
  public Duration duration;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
  public LocalDateTime createdOn = LocalDateTime.now();
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
  public LocalDateTime lastUpdate = LocalDateTime.now();
  
  @OneToMany(cascade=CascadeType.ALL)
  public List<Location> route = new ArrayList<Location>();
  
  public static Find<String, Activity> find = new Find<String, Activity>(){};

  public Activity()
  {
  }
	   
  public Activity(String activityType, String location, double distance)
  {
    this.activityType = activityType.toLowerCase();
    this.location  = location;
    this.distance  = distance;
  }
  
  //USED FOR BOOTSTRAPPING TEST DATA
  public Activity(String activityType, List<Location> locations)
  {
    this.activityType = activityType.toLowerCase();
    Location loc = locations.get(locations.size()-1);
    this.location = GoogleParser.parseLatLong(String.valueOf(loc.latitude), String.valueOf(loc.longitude));
    double dist = 0.0;
    for(int i=0; i< locations.size()-1; i++){
    	dist += GoogleParser.getDistance(locations.get(i), locations.get(i+1));
    }
    for(int i=0; i<locations.size(); i++){
    	Location l = new Location(locations.get(i).latitude,
    			locations.get(i).longitude, i);
    	this.route.add(l);
    }
    this.distance = dist;
    
    
  }
	  
  @Override
  public String toString()
  {
    return toStringHelper(this).addValue(id)
                               .addValue(activityType)
                               .addValue(location)
                               .addValue(distance)
                               .toString();
  }
	  
  @Override
  public boolean equals(final Object obj)
  {
    if (obj instanceof Activity)
    {
      final Activity other = (Activity) obj;
      return Objects.equal(activityType,      other.activityType) 
          && Objects.equal(location,  other.location)
          && Objects.equal(distance,  other.distance) ; 
    }
    else
    {
      return false;
    }
  }
	  
  @Override  
  public int hashCode()  
  {  
     return Objects.hashCode(this.id, this.activityType, this.location, this.distance);  
  } 
	
  public static Activity findById(Long id)
  {
    return find.where().eq("id", id).findUnique();
  }
  
  public static List<Activity> findAll()
  {
    return find.all();
  }

  public void update (Activity activity)
  {
    this.activityType     = activity.activityType;
    this.location = activity.location;
    this.distance = activity.distance;
  }
  
  public static void deleteAll()
  {
    for (Activity activity: Activity.findAll())
    {
      activity.delete();
    }
  }
  
  public Double getDistance(){
	  return this.distance;
  }
  
  @JSON
  public Duration getDuration(){
	  Duration d = Duration.ZERO;
	  if(route.size() > 1){
		  LocalDateTime start = route.get(0).createdOn;
		  LocalDateTime end = route.get(route.size()-1).createdOn;
		  d = Duration.between(start, end);
	  }	  
	  return d;
  }
  
  public String printDuration(){
	  return DurationFormatUtils.formatDuration(Math.abs(getDuration().toMillis()), "H:mm:ss");
  }
  
  public String getKmph(){
	  String result = "";
	  Double speed = 0.0;
	  if(route.size() > 1){
		  LocalDateTime start = route.get(0).createdOn;
		  LocalDateTime end = route.get(route.size()-1).createdOn;
		  Double kilometers = distance/1000.0;
		  
		 try{
			 Long durationInMillis = Duration.between(start, end).toMillis();
			 
			  Long durationInSeconds = durationInMillis/1000;
			  
			  
			  
			  
			  
			  
			  Double kmph = (kilometers/durationInSeconds)*60*60;
			  
			  DecimalFormat df = new DecimalFormat("####0.00");
			  speed = Double.valueOf(df.format(kmph));
			  
			  result = String.valueOf(speed);
		 }catch(NumberFormatException e){
			 result = "?";
		 }
		   
	  }	  
	  return result;
  }
  
}
