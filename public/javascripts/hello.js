if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

var friendArray = [];

var app = angular.module('pacemaker', ['angularUtils.directives.dirPagination','chart.js'])
.controller('Dashboard', function($scope, $http, $location, $window) {

	var baseUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port();
	var userid = $scope.userid;
	var resturl = baseUrl + "/api/users/" + userid + "/activities";
	var leaderboardurl = baseUrl + "/api/users/" + userid + "/leaderboard";
	console.log(resturl);
    $http.get(resturl).
        then(function(activities_response) {
        	console.log(activities_response);
            var chart1labels = Array.from(
            	new Set(activities_response.data.map(
            		function(obj){
            			return obj.activityType.toLowerCase();
            		}
            	))
            );
            var chart1data = [];
            for(var i = 0; i < chart1labels.length; i++){
            	var count = 0;
   				angular.forEach(activities_response.data, function(activity){
        			count += activity.activityType.toLowerCase() === chart1labels[i] ? 1 : 0;
    			});
    			chart1data.push( count ); 
            }
            $scope.chart1labels = chart1labels;
    		$scope.chart1data = chart1data;
    		//$scope.chart1options = {legend: {display: true}};
    		
    		var chart2labels = chart1labels;
    		var chart2data = [];
    		var chart2minData = [];
    		var chart2maxData = [];
            for(var i = 0; i < chart1labels.length; i++){
            	var min = 999999999;
            	var max = 0;
   				angular.forEach(activities_response.data, function(activity){
        			if(activity.activityType.toLowerCase() === chart1labels[i]){
        				if(activity.distance < min){
        					min = activity.distance;
        				}
        				if(activity.distance > max){
        					max = activity.distance;
        				}
        			}
    			});
    			chart2minData.push( min ); 
    			chart2maxData.push( max ); 
            }
            chart2data.push(chart2minData);
            chart2data.push(chart2maxData);
    		
    		$scope.chart2labels = chart2labels;
  	 		$scope.chart2data = chart2data;
  	 		
    		$http.get(leaderboardurl).
        		then(function(response) {
        			 $scope.sortType     = 'distanceTravelled'; 
  					 $scope.sortReverse  = true;  
  					 $scope.searchTerm   = '';     
  
        			$scope.users = response.data;
        		}).then(function(response) {
        			 $http.get(baseUrl + "/api/users/" + userid).
        				then(function(user_response) {
        					$scope.user = user_response.data;
        				});
        		});
        		
        		
  
     });
	
    
    
  	
  	
  	
})
.controller('Activity', function($scope, $http, $location, $window) {
	var baseUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port();
	var userid = $scope.userid;
	var resturl = baseUrl + "/api/users/" + userid + "/activities";
    $http.get(resturl).
        then(function(response) {
        	var acts = response.data.sort(function(a, b) {
        		var dateOne = convertJavaToJsDate(a.createdOn);
        		var dateTwo = convertJavaToJsDate(b.createdOn);
    			return dateTwo - dateOne;
			});		
			 $scope.startDate = new Date(2016,11,01,00,00,00);
             $scope.endDate = new Date(2017,11,01,00,00,00);				
            $scope.activities = acts;
        });
        
        
       
        $scope.inspectActivity = function(href){
		    $window.location.href = "/activity/" + href;
  		}	
  		
  		
  		
})
.controller('Users', function($scope, $http, $location, $window) {
	var baseUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port();
	console.log("USERS CONTROLLER ANGULAR");
	var userurl = baseUrl + "/api/users/" + $scope.userid;
	var allusersurl = baseUrl + "/api/users";
	var friendsurl = baseUrl + "/api/friends/" + $scope.userid;
    $http.get(userurl).
        then(function(user_response) {
             $scope.my = user_response.data;
             console.log("INIT");
             console.log(user_response.data);
             var friendlist = user_response.data.friends;
             friendArray = friendlist.map(function(obj){return obj.email;});
             var friendDistances = friendlist.map(function(obj){return obj.distanceTravelled;});
             var totalFriendDistance = friendDistances.reduce(function(a,b){ return a + b; }, 0);
             $scope.my.friends.distanceTravelled = totalFriendDistance;
	      			
             $http.get(allusersurl).
        	  	then(function(allusers_response) { 
        	  		var user_list = allusers_response.data.filter(function( obj ) {
    					return obj.email !== user_response.data.email;
					});	  
					for(var i = 0; i < user_list.length; i++){								
						user_list[i].is_friend = (friendArray.indexOf(user_list[i].email) > -1);							
					}    
					console.log(user_list)		
        	  		$scope.users = user_list;
		            	   
        	  	});
             	
             	
             	
             	
             	
        	 
        	
        });
        
        
       
        $scope.showUser = function(id){
    		var params = { id: id }
    		var userurl = baseUrl + "/api/users/" + id;
    		$http.get(userurl, {params: params}).
        		then(function(response) {
	        		console.log(response);
            		$scope.user = response.data;
            		console.log(friendArray);
            		$scope.user.is_friend = (friendArray.indexOf(response.data.email) > -1);
        		});
  		}	
  		
  		$scope.unfriend = function(friend_id){
    		var userurl = baseUrl + "/api/users/" + $scope.userid + "/unfriend/" + friend_id;
    		$http.post(userurl).
        		then(function(response) {
	        		console.log(response);
            		//$scope.users = response.data.friends;
            		//$scope.user = null;
            		$window.location.reload();
        		});
    		
  		}	
  		
  		$scope.friend = function(friend_id){

    		var userurl = baseUrl + "/api/users/" + $scope.userid + "/addfriend/" + friend_id;
    		console.log(userurl);
    		$http.post(userurl).
        		then(function(response) {
	        		console.log(response);
            		//$scope.users = response.data.friends;
            		$window.location.reload();
        		});
  		}	
})
.controller('ShowActivity', function($scope, $http, $location, $window) {
	var baseUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port();	
	var resturl = baseUrl + "/api/users/" + $scope.userid + "/activities/" + $scope.activityid;
	
        
      $scope.editActivity = function(){    	
    	$window.location.href = "/activity/edit/" + $scope.activityid;
  	  }	
  	  
  	   $scope.deleteActivity = function(id){    	
    		$http.delete(resturl).
       			then(function(response) {
            		console.log(response);		
            		$window.location.href = "/activities"
        		});
  	  }	
        
})
.controller('ActivityFeed', function($scope, $http, $location, $window) {
	var baseUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port();
	var friendsurl = baseUrl + "/api/friends/" + $scope.userid;
	var objsArray = [];
	
	var idx = 0;
    $http.get(friendsurl).
        then(function(feed) {
        	angular.forEach( feed.data, function ( f ) {
  				angular.forEach( f.activities, function ( a ) {
  					objsArray[idx] = {
            			distanceTravelled: f.distanceTravelled, 
            			email: f.email, 
            			activity: a.activityType,
            			distance: a.distance,
            			location: a.location,            			
            			lastUpdate: new Date(
            				a.lastUpdate.year, a.lastUpdate.monthValue, a.lastUpdate.dayOfMonth,
            				a.lastUpdate.hour, a.lastUpdate.minute, a.lastUpdate.second, 0
            			),
            			formattedDate: a.lastUpdate.dayOfMonth + "-" +
            				a.lastUpdate.monthValue + "-" + a.lastUpdate.year + " " +
            				a.lastUpdate.hour + ":" + a.lastUpdate.minute + ":" + a.lastUpdate.second
            		};
            		idx++;
				});
			});
			objsArray = objsArray.sort(function(a, b) {
    			return b.lastUpdate - a.lastUpdate;
			});
    		
    		$scope.feed = objsArray;
			
           
      		
    		
        });
        
    
    
    
    
    
   
	
  			
});

function convertJavaToJsDate(javadate){
	//month index in javascript is different to java, index starts at zero
	return new Date(
      javadate.year, javadate.monthValue-1, javadate.dayOfMonth,
      javadate.hour, javadate.minute, javadate.second, 0
    );
}

angular.module('pacemaker').filter('dateRangeFilter', function() {
  		return function(activities, start, end) {
  				var result = [];
        		if(activities != null){
        			startDate = new Date(start),
            		endDate = new Date(end);
		        	for (var i=0, len = activities.length; i < len; i++) {
	            		var inputDate = convertJavaToJsDate(activities[i].lastUpdate);            
            			if (startDate < inputDate && inputDate < endDate) {
            				console.log(startDate + " -> " + inputDate + " -> " + endDate );
		               		result.push(activities[i]);
            			}  
        			}       
        			return result;          
        		}
        		return result
            	  
  			};
});