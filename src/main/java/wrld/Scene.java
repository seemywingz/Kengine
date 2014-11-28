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
import javafx.scene.media.AudioClip;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.Clip;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
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
    protected Point centerPoint;
    protected Camera camera;

    //protected static Textures textures;

    // JBullet Components
    static boolean
            solving,mkCenterPoint;
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
        gl.glEnable(GL2.GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // best perspective correction
        gl.glShadeModel(GL2.GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        gl.glEnable(GL2.GL_GENERATE_MIPMAP);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glPolygonOffset(2.5f, 0.0f);
        //enable transparency
        gl.glEnable (GL2.GL_BLEND);
        gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //alpha testing -behing obj
        gl.glAlphaFunc(GL2.GL_GREATER, 0.5f);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glActiveTexture(GL2.GL_TEXTURE0);

        initializePhysics();
        camera=new Camera(gl);

        ObjectArrayList<Vector3f> points;
        ObjectLoader objectLoader = new ObjectLoader(gl);
        int callist;

        points = new ObjectArrayList<Vector3f>();// Map
        callist = objectLoader.LoadOBJ("/obj/map/","map1.obj",points,null);
        models.add(new ConcaveCollisionModel(new Point3d(0,-200,-10,2000f),world,points,callist));//*/

        points = new ObjectArrayList<Vector3f>();// Tree
        callist = objectLoader.LoadOBJ("/obj/tree/","tree.obj",points,null);
        models.add(new ConvexCollisionModel(new Point3d(30,0,0,.04f),world,points,callist));//*/

        points = new ObjectArrayList<Vector3f>();// woodHouse
        callist = objectLoader.LoadOBJ("/obj/woodhouse/","woodhouse.obj",points,null);
        models.add(new ConcaveCollisionModel(new Point3d(50,0,0,.05f),world,points,callist));//*/

        points = new ObjectArrayList<Vector3f>();// chair
        callist = objectLoader.LoadOBJ("/obj/tableNchair/","table.obj",points,null);
        models.add(new ConvexCollisionModel(new Point3d(55,1,0,.8f,18.1437f),world,points,callist));//*/

        /*Vector<Integer>frames = new Vector<Integer>();// Skleton
        objectLoader.loadAnimation(frames,"/obj/Skeleton/","Skeleton",20);
        models.add(new Skeleton(gl, new Point3d(-90, 1.5f, 5, 1, 3f),world, frames));// world, points, callist));//*/


        models.add(new FloorModel(Textures.grass,new Point3d(0,0), world));
        models.add(new SkyDome(new Point3d(0,-500,0,1)));

        balls.add(b = new BasketBall(new Point3d(0, 1000, 5), world));

        mkBlockPyramid(-100, 0);
        mkBlockTower(-100, 50);

        Clip wind = Utils.mkClip("/snd/wind.wav");
        wind.loop(Clip.LOOP_CONTINUOUSLY);

        Clip theme = Utils.mkClip("/snd/theme1.wav");
        theme.loop(Clip.LOOP_CONTINUOUSLY);

        lastRenderTime = System.currentTimeMillis();
    }//..

    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glLoadIdentity();  // reset the model-view matrix
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glEnable(GL2.GL_BLEND);

        camera.setView();
        Light.sun(gl);

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

        if(timer==10000){
            camera.fog=true;
            timer+=20;
        }else{
            timer+=20;
        }//*/

        if(step<20){// keep frame rate on faster machines
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
        gl.glMatrixMode(GL2.GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // mouseCenter projection matrix
        glu.gluPerspective(45.0, aspect, .1, 11000); // fovy, aspect, zNear, zFar

                // Enable the model-setView transform
        gl.glMatrixMode(GL2.GL_MODELVIEW);
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

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    kengine.dispatchEvent(new WindowEvent(kengine, WindowEvent.WINDOW_CLOSING));
                }
            }
        };
    }//..

    protected void centerCursor(int mouseCenter){
        Point p = kengine.getLocation();
        int cx = p.x+mouseCenter,
                cy = p.y+mouseCenter;
        rob.mouseMove(cx, cy);
        //System.out.println("r: "+cy);
        //return new Point(cx,cy);
    }//...

    protected MouseAdapter mkMouseAdapter(){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }//..
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    if(kengine.openJDK){
                        System.out.println("m: " + e.getX() + "\nmc: " + mouseCenter + "\ncp: " + centerPoint.y);
                        camera.look(e.getX() - 975, e.getY(), mouseCenter);
                        centerCursor(mouseCenter);
                    }else {
                        camera.look(e.getX(), e.getY(), mouseCenter);
                        centerCursor(mouseCenter);
                    }
                }catch (Exception ex){}
                super.mouseMoved(e);
            }//..

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(camera.holding){
                    camera.throwObj();
                }else if(e.getButton() == MouseEvent.BUTTON1){
                    if(e.isShiftDown()){
                        shootBall(BallType.cannonBall);
                    }else{
                        shootBall(BallType.basketBall);
                    }

                }

            }
        };
    }//..

    protected void shootBall(BallType type){
        switch (type){
            case basketBall:
                addLogic =new Logic() {
                    @Override
                    public void apply() throws Exception {
                        Vector3f c = camera.getPosition(1);
                        balls.add(b = new BasketBall(new Point3d(c.x,c.y,c.z), world));
                        b.setVelocity(camera.getDirection(10,0));
                        balls.add(b);
                        addLogic =null;
                    }
                };
                break;
            case cannonBall:
                addLogic =new Logic() {
                    @Override
                    public void apply() throws Exception {
                        Vector3f c = camera.getPosition(1);
                        balls.add(b = new CannonBall(new Point3d(c.x,c.y,c.z), world));
                        b.setVelocity(camera.getDirection(50,0));
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

    protected void mkBlockPyramid(int strtx, int srtrz){
        float mass = 20,sz = .5f, sep = sz*2; //  a pyramid of boxes
        int b = 9;
        for (int i=0;i<5;i++){
            for(int k=b;k>0;k--){//depth
                for(int j=b;j>0;j--){//width
                    boxes.add( new Cube(new Point3d(strtx+j*sep,(sz)+(sz*i*2),srtrz+k*sep, sz,mass),world,Textures.box));
                }
            }
            b-=2;
            strtx+=sep;
            srtrz+=sep;
        }
    }//..

    protected void mkBlockTower(int strtx, int srtrz){
        float mass = 20,sz = 0.5f, sep = sz*2; //  a tower of boxes
        for(int k=0;k<4;k++)//depth
            for(int j=0;j<6;j++){//width
                for(int i=0;i<10;i++){//height
                    boxes.add( new Cube(new Point3d(strtx+j*sep,(sz)+(sz*i*2),srtrz+k*sep, sz,mass),world,Textures.box));
                }
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
                //return getGroundAtPoint(x+2,z+2);
            }else{
                rayResult.hitPointWorld.get(hit);
            }
        }
        return hit;
    }//..


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
