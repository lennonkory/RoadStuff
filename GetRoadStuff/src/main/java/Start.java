import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// http://gis.stackexchange.com/questions/157693/getting-all-vertex-lat-long-coordinates-every-1-meter-between-two-known-points
public class Start {

    private class LatLng {

	private double lat;
	private double lng;

	public LatLng(double lat, double lng) {
	    this.lat = lat;
	    this.lng = lng;
	}

	public double getLat() {
	    return lat;
	}

	public void setLat(double lat) {
	    this.lat = lat;
	}

	public double getLng() {
	    return lng;
	}

	public void setLng(double lng) {
	    this.lng = lng;
	}

	public void print() {
	    System.out.println(this.lat + "," + lng);
	}

    }

    public static void main(String[] args) throws Exception {

	Start start = new Start();
	start.createTrip();

    }

    private void createTrip() {
	// AIzaSyAxVWt7yuVpUrRSnMHzLOJF4iqYzGRotfk

	/*
	 * GeoApiContext context = new
	 * GeoApiContext().setApiKey("AIzaSyAxVWt7yuVpUrRSnMHzLOJF4iqYzGRotfk");
	 * GeocodingResult[] results = GeocodingApi.geocode(context,
	 * "Waterloo ON").await(); LatLng ll = results[0].geometry.location;
	 */

	/*
	 * Find start point with in Radius - this can be random
	 */

	double lat = 43.46425780;
	double lng = -80.52040960;

	LatLng start = getLocation(lat, lng, 1500);
	LatLng end = getLocation(start, 10000, 1.0, 0.9);

	double interval = 100.0;

	// LatLng start = new LatLng(43.97076, 12.72543);

	// LatLng end = new LatLng(43.969730, 12.728294);

	double azimuth = calculateBearing(start, end);
	System.out.println(azimuth);

	List<LatLng> coords = findPoints(interval, azimuth, start, end);

	for (LatLng ll : coords) {
	    ll.print();
	}

	for (int i = 0; i < 1; i++) {
	    // getLocation(start, 10000, 1.0, 0.9);
	}
    }

    public LatLng getLocation(double lat, double lng, int radius) {

	Random ran = new Random();
	LatLng latlng = new LatLng(lat, lng);
	return getLocation(latlng, radius, ran.nextDouble(), ran.nextDouble());
    }

    public LatLng getLocation(LatLng start, int radius, double rand1, double rand2) {

	// Convert radius from meters to degrees
	double radiusInDegrees = radius / 111300f;

	double w = radiusInDegrees * Math.sqrt(rand1);
	double t = 2 * Math.PI * rand2;
	double x = w * Math.cos(t);
	double y = w * Math.sin(t);

	// Adjust the x-coordinate for the shrinking of the east-west distances
	double new_x = x / Math.cos(Math.toRadians(start.lat));

	double foundLongitude = new_x + start.lng;
	double foundLatitude = y + start.lat;

	LatLng newLatLng = new LatLng(foundLatitude, foundLongitude);
	newLatLng.print();
	distance(start, newLatLng);

	return newLatLng;

    }

    public double distance(LatLng start, LatLng end) {

	final float R = 6372.796924F; // Radius of the earth

	Double latDistance = Math.toRadians(end.lat - start.lat);
	Double lonDistance = Math.toRadians(end.lng - start.lng);
	Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(start.lat))
		* Math.cos(Math.toRadians(end.lat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	double distance = R * c * 1000; // convert to meters

	distance = Math.pow(distance, 2) + Math.pow(0.0, 2);

	System.out.println("Distance: " + (Math.sqrt(distance)));

	return Math.sqrt(distance);
    }

    /*
     * returns the lat an long of destination point given the start lat, long,
     * aziuth, and distance
     */
    public LatLng getDestinationLatLong(LatLng latlng, double azimuth, double distance) {

	double R = 6378.1; // Radius of the Earth in km
	double brng = Math.toRadians(azimuth); // Bearing is degrees converted
					       // to radians.
	double d = distance / 1000; // Distance m converted to km
	double lat1 = Math.toRadians(latlng.lat); // Current dd lat point
						  // converted to
	// radians
	double lon1 = Math.toRadians(latlng.lng); // Current dd long point
						  // converted to
	// radians
	double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R) + Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
	double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
		Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

	// convert back to degrees
	lat2 = Math.toDegrees(lat2);
	lon2 = Math.toDegrees(lon2);
	return new LatLng(lat2, lon2);

    }

    // calculates the azimuth in degrees from start point to end point
    public double calculateBearing(LatLng start, LatLng end) {

	double startLat = Math.toRadians(start.lat);
	double startLong = Math.toRadians(start.lng);
	double endLat = Math.toRadians(end.lat);
	double endLong = Math.toRadians(end.lng);
	double dLong = endLong - startLong;
	double dPhi = Math.log(Math.tan(endLat / 2.0 + Math.PI / 4.0) / Math.tan(startLat / 2.0 + Math.PI / 4.0));
	if (Math.abs(dLong) > Math.PI) {
	    if (dLong > 0.0) {
		dLong = -(2.0 * Math.PI - dLong);
	    } else {
		dLong = (2.0 * Math.PI + dLong);
	    }
	}
	double bearing = (Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;
	return bearing;
    }

    /*
     * returns every coordinate pair inbetween two coordinate pairs given the
     * desired interval
     */

    public List<LatLng> findPoints(double interval, double azimuth, LatLng start, LatLng end) {

	double d = distance(start, end);
	double dist = Math.floor(d) / interval;
	double counter = interval;

	List<LatLng> coords = new ArrayList<>();
	coords.add(start);

	for (int i = 0; i < (int) dist; i++) {
	    LatLng coord = getDestinationLatLong(start, azimuth, counter);
	    counter = counter + interval;
	    coords.add(coord);
	}

	coords.add(end);
	return coords;

    }

}
