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

    ConvexCollisionModel(GL2 gl, Point3d p, DynamicsWorld world, ObjectArrayList<Vector3f> points, int callist){
        super(gl,p,callist);
        this.points=points;
        shape = new ConvexHullShape(points);
        initializePhysics(world);
    }//..

    @Override
    public void draw() {
        t = body.getWorldTransform(t);
        t.getOpenGLMatrix(glMatrix);

        gl.glPushMatrix();

        gl.glMultMatrixf(Utils.mkFloatBuffer(glMatrix));
        body.getCollisionShape().setLocalScaling(new Vector3f(p.size,p.size,p.size));
        gl.glScaled(p.size,p.size, p.size);
        //ShowLines();
        gl.glCallList(callist);
        gl.glDisable(gl.GL_COLOR_MATERIAL);
        gl.glPopMatrix();

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
