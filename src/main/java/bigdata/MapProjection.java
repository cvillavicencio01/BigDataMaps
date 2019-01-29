

/**
 * MapProjection contains the function used to locate Points 
 * corresponding to tiles
 */
public class MapProjection {
	private final int TILE_SIZE = 256;
    private Point pixelOrigin;
    private double pixelsPerLonDegree;
    private double pixelsPerLonRadian;

    public MapProjection() {
        pixelOrigin = new Point(TILE_SIZE / 2.0,TILE_SIZE / 2.0);
        pixelsPerLonDegree = TILE_SIZE / 360.0;
        pixelsPerLonRadian = TILE_SIZE / (2 * Math.PI);
    }

    double bound(double val, double valMin, double valMax) { 
        double res;
        res = Math.max(val, valMin);
        res = Math.min(res, valMax);
        return res;
    }

    double degreesToRadians(double deg) {
        return deg * (Math.PI / 180);
    }

    /**
	 * Uses a latitude and a longitude to return a corresponding Point
	 * @param lat Latitude 
     * @param lng Longitude
     * @param zoom Zoom
	 * @return Point
	 */
    Point fromLatLngToPoint(double lat, double lng, int zoom) {
        Point point = new Point(0, 0);

        point.x = pixelOrigin.x + lng * pixelsPerLonDegree;       

        double siny = bound(Math.sin(degreesToRadians(lat)), -0.9999,0.9999);
        point.y = pixelOrigin.y + 0.5 * Math.log((1 + siny) / (1 - siny)) *- pixelsPerLonRadian;

        int numTiles = 1 << zoom;
        point.x = point.x * numTiles;
        point.y = point.y * numTiles;
        return point;
     }
    
     /**
	 * Uses a latitude and a longitude to return the Point corresponding to a tile position
	 * @param lat Latitude 
     * @param lng Longitude
     * @param zoom Zoom
	 * @return void
	 */
    
    public Point degToTilePosition(double lat, double lon, int zoom) {
    	double latRad = Math.toRadians(lat);
    	double n = Math.pow(2.0, zoom);
    	
    	int x = (int) ((lon+180.0)/ 360.0 * n);
    	int y = (int) ((1.0 - Math.log(Math.tan(latRad) + (1 / Math.cos(latRad))) / Math.PI) / 2.0 * n);
    	
		return new Point(x,y);
    }
    
    class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

		public double getX() {
			return x;
		}

		public int getIntX() {
			return (int) x;
		}
		
		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}
		
		public int getIntY() {
			return (int) y;
		}

		public void setY(double y) {
			this.y = y;
		} 
    }    
}
