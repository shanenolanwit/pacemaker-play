package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.toStringHelper;
import com.google.common.base.Objects;

import flexjson.JSON;

import javax.persistence.*;
import com.avaje.ebean.Model;

@Entity
@Table(name="my_user")
public class User extends Model
{
  @Id
  @GeneratedValue
  public Long   id;
  public String firstname;
  public String lastname;
  @Basic(optional=false) 
  @Column(unique=true)
  public String email;
  @JSON(include=false)
  @Basic(optional=false)
  public String password;
  
  public Integer phaseInterval = 1000;
  
  public String apiKey = "coming soon";
  

  
  @Transient
  public Double distanceTravelled;
  
  @OneToMany(cascade=CascadeType.ALL)
  public List<Activity> activities = new ArrayList<Activity>();
  
  @ManyToMany(cascade=CascadeType.ALL)
  @JoinTable(       name = "user_user",
      joinColumns = @JoinColumn(name = "source_user_id"),
      inverseJoinColumns = @JoinColumn(name = "target_user_id"))
  public List<User> friends = new ArrayList<User>();

  public static Find<String, User> find = new Find<String, User>(){};

  public User()
  {
  }
  
  @JSON
  public Double getDistanceTravelled(){
	  Double distance = 0.0;
	  if(activities.size() > 0){
		  distance = activities.stream()
	              .collect(Collectors.summingDouble(Activity::getDistance));
	  }
	 return distance;
  }

  public User(String firstname, String lastname, String email, String password)
  {
    this.firstname = firstname;
    this.lastname  = lastname;
    this.email     = email;
    this.password  = password;
  }

  public void update (User user)
  {
    this.firstname = user.firstname;
    this.lastname  = user.lastname;
    this.email     = user.email;
    this.password  = user.password;
  }

  public String toString()
  {
    return toStringHelper(this)
        .add("Id", id)
        .add("Firstname", firstname)
        .add("Lastname", lastname)
        .add("Email", email)
        .add("Friends", friends).toString();
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (obj instanceof User)
    {
      final User other = (User) obj;
      return Objects.equal(firstname, other.firstname) 
          && Objects.equal(lastname, other.lastname)
          && Objects.equal(email, other.email);
    }
    else
    {
      return false;
    }
  }

  public static User findByApiKey(String apiKey)
  {
    return  User.find.where().eq("apiKey", apiKey).findUnique();
  }
  
  public static User findByEmail(String email)
  {
    return  User.find.where().eq("email", email).findUnique();
  }

  public static User findById(Long id)
  {
    return find.where().eq("id", id).findUnique();
  }

  public static List<User> findAll()
  {
    return find.all();
  }

  public static void deleteAll()
  {
    for (User user: User.findAll())
    {
      user.delete();
    }
  } 

}
