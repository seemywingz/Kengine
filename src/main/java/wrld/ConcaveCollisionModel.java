package wrld;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Kevin on 1/8/14.
 */
public class ConcaveCollisionModel extends ConvexCollisionModel {


    ConcaveCollisionModel(Point3d p, DynamicsWorld world, ObjectArrayList<Vector3f> points, int callist) {
        super(p, world, points, callist);
    }

    @Override
    protected void initializePhysics(DynamicsWorld world) {

        float coords[] = new float[points.size()*3];
        int indices[] = new int[points.size()];
        int j = 0;
        for(int i=0;i<points.size();i++){
            Vector3f v = points.get(i);
            coords[j]=v.x;
            coords[j+1]=v.y;
            coords[j+2]=v.z;
            j+=3;
        }
        for (int i=0;i<indices.length;i++){
            indices[i]=i;
        }

        IndexedMesh indexedMesh = new IndexedMesh();
        indexedMesh.numTriangles = indices.length / 3;
        indexedMesh.triangleIndexBase = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder());
        indexedMesh.triangleIndexBase.asIntBuffer().put(indices);
        indexedMesh.triangleIndexStride = 3 * 4;
        indexedMesh.numVertices = coords.length / 3;
        indexedMesh.vertexBase = ByteBuffer.allocateDirect(coords.length*4).order(ByteOrder.nativeOrder());
        indexedMesh.vertexBase.asFloatBuffer().put(coords);
        indexedMesh.vertexStride = 3 * 4;

        TriangleIndexVertexArray vertArray = new TriangleIndexVertexArray();
        vertArray.addIndexedMesh(indexedMesh);

        boolean useQuantizedAabbCompression = false;
        BvhTriangleMeshShape meshShape = new BvhTriangleMeshShape(vertArray, useQuantizedAabbCompression);

        MotionState motionState;

        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(p.x,p.y,p.z));
        Vector3f inertia = new Vector3f(0,0,0);
        meshShape.calculateLocalInertia(p.mass,inertia);

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(p.mass,motionState,meshShape,inertia);
        body = new RigidBody(info);
        body.setFriction(1f);
        body.setDamping(.3f,.3f);
        body.setRestitution(.5f);
        body.getCollisionShape().setLocalScaling(new Vector3f(p.size,p.size,p.size));

        world.addRigidBody(body);
    }
}// end Class ConcaveCollisionModel
