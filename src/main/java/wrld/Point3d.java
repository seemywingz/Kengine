package wrld;

import javax.vecmath.Vector3f;

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
	
}// end wrld.Point3d
