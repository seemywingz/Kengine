package wrld;

import javax.media.opengl.awt.GLCanvas;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.Clip;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.util.Vector;

/**
 * Created by kevin on 11/13/14.
 */
public class Scene implements GLEventListener{

    protected static Kengine kengine;
    protected static FPSAnimator fpsa;
    protected GLCanvas canvas;
    protected GLU glu;
    protected static GL2 gl;

    protected Robot rob;
    protected int mouseCenter;
    protected Camera camera;

    //protected static Textures textures;

    // JBullet Components
    static boolean
            solving;
    static DiscreteDynamicsWorld world;
    Vector<RigidBody> boundingBox = new Vector<RigidBody>();
    static final float FPS=1f/20f;
    long lastRenderTime;

    // OpenGL Models
    Vector<Model> models = new Vector<Model>();
    protected Vector<Ball> balls = new Vector<Ball>();
    Vector<Cube> boxes = new Vector<Cube>();
    FloorModel floor;
    SkyDome sky;
    Ball b;

    int timer = 0;
    Logic addLogic = null;

    Scene(Kengine kengine){
        this.kengine = kengine;
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        canvas = new GLCanvas(glcapabilities);
        canvas.setSize(kengine.getWidth(), kengine.getHeight());
        canvas.addGLEventListener(this);
        canvas.addMouseMotionListener(mkMouseAdapter());
        canvas.addMouseListener(mkMouseAdapter());
        canvas.addKeyListener(mkKeyAdapter());
        canvas.requestFocus();

        fpsa = new FPSAnimator(canvas,60,true);
        fpsa.start();

        kengine.add(canvas);

        hideCursor();
        try {
            rob = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        mouseCenter = (int) (kengine.getHeight()*.5);
    }//..

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(gl.GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(gl.GL_LEQUAL);  // the type of depth test to do
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST); // best perspective correction
        gl.glShadeModel(gl.GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        gl.glEnable(gl.GL_GENERATE_MIPMAP);
        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_NORMALIZE);
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glPolygonOffset(2.5f, 0.0f);
        //enable transparency
        gl.glEnable (gl.GL_BLEND);
        gl.glBlendFunc (gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        //alpha testing -behing callist
        gl.glAlphaFunc(gl.GL_GREATER, 0.5f);
        gl.glEnable(gl.GL_ALPHA_TEST);
        gl.glActiveTexture(gl.GL_TEXTURE0);

        initializePhysics();
        camera=new Camera(gl);

        ObjectArrayList<Vector3f> points;
        ObjectLoader objectLoader = new ObjectLoader(gl);
        int callist;

        points = new ObjectArrayList<Vector3f>();// Map
        callist = objectLoader.LoadOBJ("/obj/map/","map1.obj",points,null);
        models.add(new ConcaveCollisionModel(gl,new Point3d(0,-200,-10,3000f),world,points,callist));

        points = new ObjectArrayList<Vector3f>();// Tree
        callist = objectLoader.LoadOBJ("/obj/tree/","tree.obj",points,null);
        models.add(new ConvexCollisionModel(gl,new Point3d(50,0,0,.04f),world,points,callist));

        /*Vector<Integer>frames = new Vector<Integer>();// Skleton A
        objectLoader.loadAnimation(frames,"/obj/Skeleton/","Skeleton",20);
        models.add(new Skeleton(gl, new Point3d(-90, 1.5f, 5, 1, 3f),world, frames));// world, points, callist));*/

        balls.add(b = new BasketBall(gl, new Point3d(0, 1000, 5), world));

        floor = new FloorModel(gl,Textures.grass,new Point3d(0,0), world);
        sky = new SkyDome(gl,Textures.sky,new Point3d(0,-500,0,1));

        mkPyramid(-100,0);

        final Clip wind = Utils.mkClip(getClass(),"/snd/wind.wav");
       /* FloatControl gainControl = (FloatControl) wind.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.    */
        wind.loop(Clip.LOOP_CONTINUOUSLY);

        lastRenderTime = System.currentTimeMillis();
    }//..

    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glLoadIdentity();  // reset the model-view matrix
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glEnable(gl.GL_BLEND);

        camera.setView();
        Light.sun(gl);
        sky.draw();
        floor.draw();

        long curRenderTime = System.currentTimeMillis();
        float step = (float)(curRenderTime - lastRenderTime);
        solving = true;
            world.stepSimulation(1f/step);
            lastRenderTime =System.currentTimeMillis();
        solving = false;

        for(Model m:models){
            m.draw();
        }
        for (Ball b: balls){
            b.draw();
        }
        for (Cube c:boxes){
            c.draw();
        }

        try {
            if(addLogic != null)
                addLogic.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if(timer==10000){
            camera.fog=true;
            timer+=20;
        }else{
            timer+=20;
        }**/
        if(step<20){
            Utils.wait((int) (20-step));
        }
    }//..

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) height = 1;   // prevent divide by zero
        float aspect = (float)width / height;

               // Set the setView port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

               // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(gl.GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // mouseCenter projection matrix
        glu.gluPerspective(45.0, aspect, .1, 11000); // fovy, aspect, zNear, zFar

                // Enable the model-setView transform
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity(); // mouseCenter
    }//..

    @Override
    public void dispose(GLAutoDrawable drawable) {
        fpsa.stop();
    }//..


    protected KeyAdapter mkKeyAdapter(){
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                camera.keyDown(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                camera.keyUp(e);

                if(e.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('p')){
                    camera.setPosition(new Point3d(0,5,0,camera.p.size,camera.p.mass));
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    kengine.dispatchEvent(new WindowEvent(kengine, WindowEvent.WINDOW_CLOSING));
                }
            }
        };
    }//..

    protected MouseAdapter mkMouseAdapter(){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }//..
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                try {
                    camera.look(e.getX(), e.getY(), mouseCenter);
                    centerCursor(mouseCenter);
                }catch (Exception ex){}
            }//..

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(e.getButton() == MouseEvent.BUTTON1){
                    if(e.isShiftDown()){
                        shootBall(1);
                    }else{
                        shootBall(1);
                    }

                }

            }
        };
    }//..

    protected void shootBall(int type){
        switch (type){
            case 1:
                addLogic =new Logic() {
                    @Override
                    public void apply() throws Exception {
                        Vector3f c = camera.getPosition(1);
                        balls.add(b = new BasketBall(gl, new Point3d(c.x,c.y,c.z), world));
                        b.setVelocity(camera.getDirection(10,0));
                        balls.add(b);
                        addLogic =null;
                    }
                };
                break;
        }
    }//..

    protected void initializePhysics(){
        // JBullet Stuff
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);
        float bound = 800;
        Vector3f worldAabbMin = new Vector3f(-bound, -bound, -bound);
        Vector3f worldAabbMax = new Vector3f(bound, bound, bound);
        int maxProxies = 5000;
        AxisSweep3 overlappingPairCache =
                new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        BroadphaseInterface broadphase = new DbvtBroadphase();
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        world = new DiscreteDynamicsWorld(
                dispatcher,overlappingPairCache, solver,
                collisionConfiguration);
        world.setGravity(new Vector3f(0, -9.8f, 0));
    }//..

    protected void mkBoundingBox(){
        // floor
        float mass = 0;
        MotionState motionState;
        CollisionShape shape = new BoxShape(new Vector3f(3000,010,3000));


        Transform t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(300, 550, 0));

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass,motionState,shape);
        RigidBody body = new RigidBody(info);
        //body.setFriction(1f);
        // body.setDamping(.3f,.3f);
        boundingBox.add(body);
        world.addRigidBody(body);

        // top
        shape = new BoxShape(new Vector3f(3000,010,3000));

        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(300, 5, 0));

        motionState = new DefaultMotionState(t);
        info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        world.addRigidBody(body);

        //left wall
        mass = 0;
        shape = new BoxShape(new Vector3f(3,3000,3000));


        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(5, 0, 0));

        motionState = new DefaultMotionState(t);
        info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        world.addRigidBody(body);

        // right wall
        mass = 0;
        shape = new BoxShape(new Vector3f(3,3000,3000));


        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(890, 0, 0));

        motionState = new DefaultMotionState(t);
        info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        world.addRigidBody(body);
    }//..

    protected void mkPyramid(int strtx, int srtrz){
        float mass = 20,sz = .5f, sep = sz*2; //  a pyramid of boxes
        int b = 9;
        for (int i=0;i<5;i++){
            for(int k=b;k>0;k--){//depth
                for(int j=b;j>0;j--){//width
                    boxes.add( new Cube(gl,new Point3d(strtx+j*sep,(sz)+(sz*i*2),srtrz+k*sep, sz,mass),world,Textures.box));
                }
            }
            b-=2;
            strtx+=sep;
            srtrz+=sep;
        }
    }//..

    protected float[] getGroundAtPoint(double x, double z){
        float y = 1000;
        float hit[] = new float[3];
        Vector3f pointFrom =  new Vector3f((float)x,y,(float)z), pointTo = new Vector3f((float)x,-y,(float)z);
        CollisionWorld.ClosestRayResultCallback rayResult = new CollisionWorld.ClosestRayResultCallback(pointFrom,pointTo);
        world.rayTest(pointFrom, pointTo, rayResult);
        if(rayResult.hasHit()){
            if(rayResult.collisionObject != models.get(0).getBody()){ // if not the ground
                return getGroundAtPoint(x+2,z+2);
            }else{
                rayResult.hitPointWorld.get(hit);
            }
        }
        return hit;
    }//..

    protected void centerCursor(int mouseCenter){
        Point p = kengine.getLocation();
        int cx = p.x+mouseCenter,
            cy = p.y+mouseCenter;
        rob.mouseMove(cx, cy);
    }//...

    protected void hideCursor(){
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        kengine.setCursor(blankCursor);
    }//..

    protected void showCursor(){
        kengine.setCursor(Cursor.getDefaultCursor());
    }//..

    protected static void safeAddRigidBody(RigidBody body){
        while (solving){Utils.wait(1);}
        world.addRigidBody(body);
    }//...

}// wrld.Scene
