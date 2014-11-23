package wrld;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.util.ObjectArrayList;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 * Created by Kevin on 1/8/14.
 */
public class ConvexCollisionModel extends CollisionModel {

    boolean showLines;

    protected ObjectArrayList<Vector3f> points = new ObjectArrayList<Vector3f>();

    ConvexCollisionModel(Point3d p, DynamicsWorld world, ObjectArrayList<Vector3f> points, int callist){
        super(p,callist);
        this.points=points;
        shape = new ConvexHullShape(points);
        initializePhysics(world);
    }//..

    @Override
    public void draw() {
        super.draw();
        //ShowLines();
    }//..

    protected void ShowLines(){
            gl.glBegin(gl.GL_LINE_STRIP);
            gl.glLineWidth(3.5f);
            for (int i=0;i<points.size();i++){
                Vector3f pnt = points.get(i);
                gl.glVertex3f(pnt.x,pnt.y, pnt.z);
            }
            gl.glEnd();
    }//..


}// end Class CollisionModel
