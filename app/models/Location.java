package models;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.google.common.base.Objects;

import parsers.GoogleParser;

import javax.persistence.*;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="my_location")
public class Location extends Model {
	@Id
	@GeneratedValue
	public Long id;
	
	public float latitude;
	public float longitude;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	public LocalDateTime createdOn = LocalDateTime.now();
	
	public static Find<String, Location> find = new Find<String, Location>(){};

	public Location() {
	}
	
	public Location(float latitude, float longitude, int index) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	 public static Location findById(Long id)
	 {
	   return find.where().eq("id", id).findUnique();
	 }
	 
	 public static List<Location> findAll()
	  {
	    return find.all();
	  }

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Location) {
			final Location other = (Location) obj;
			return Objects.equal(latitude, other.latitude) && Objects.equal(longitude, other.longitude);
		} else {
			return false;
		}
	}

	public String toString() {
		return toStringHelper(this)
				.add("Latitude", latitude)
				.add("Longitude", longitude).toString();
	}
	
	public String getAddress(){
		String address = latitude + "," + longitude;
		try{
			address = GoogleParser.parseLatLong(String.valueOf(latitude), String.valueOf(longitude));
		}catch(Exception e){
			
		}			
		return address;
	}
	
	public Long getDateInMillis(){
		return this.createdOn.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}