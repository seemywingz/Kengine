package wrld;

public class Point3d {

    /* wrld.Point3d Coordinates, Size and Mass */
	float x,y,z, size, mass;

    Point3d(float x, float y, float z, float size, float mass){
        this.x =x;
		this.y =y;
		this.z =z;
		this.size =size;
		this.mass = mass;
    }//..

	Point3d(float x, float y, float z, float size){
		this.x =x;
		this.y =y;
		this.z =z;
		this.size =size;
	}//..

	Point3d(float x, float y, float z){
		this.x =x;
		this.y =y;
		this.z =z;
	}//..

	Point3d(float x, float y){
		this.x =x;
		this.y =y;
	}//..

    public void set(Point3d p){
        x=p.x;y=p.y;x=p.z;
		size =p.size;
		mass =p.mass;
    }//..

	public static float calcDistance(Point3d p1, Point3d p2){
		return (float) Math.sqrt(Math.pow(p1.z-p2.z,2) + Math.pow(p1.x-p2.x,2));
	}//..
	
}// end wrld.Point3d
