package wrld;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;
import java.awt.event.KeyEvent;

/**
 * Created by kevin on 11/13/14.
 */
public class Camera {

    GL2 gl;
    Point3d pos;

    //JBullet Variables
    protected Transform t;
    protected RigidBody body;
    protected float glMatrix[] = new float[16];

    protected boolean
            keyPressed[] = new boolean[256],
            flying,
            jumping=false,
            fog;

    protected final int
    w = KeyEvent.getExtendedKeyCodeForChar('w'),
    a = KeyEvent.getExtendedKeyCodeForChar('a'),
    s = KeyEvent.getExtendedKeyCodeForChar('s'),
    d = KeyEvent.getExtendedKeyCodeForChar('d'),
    f = KeyEvent.getExtendedKeyCodeForChar('f'),
    z = KeyEvent.getExtendedKeyCodeForChar('z'),
    p = KeyEvent.getExtendedKeyCodeForChar('p'),
    space = KeyEvent.VK_SPACE,
    shift = KeyEvent.VK_SHIFT,
    ctrl = KeyEvent.VK_CONTROL;

    protected float
            xrot,
            yrot,
            xrotrad,
            yrotrad,
            maxSpeed = 8,
            walkSpeed = 20,
            runSpeed = 50,
            flySpeed = .5f,
            radius = .5f;
    Vector3f velocity = new Vector3f();

    Camera(GL2 gl) {
        this.gl = gl;
        pos = new Point3d(0,10,0,1.8f,83.9146f);
        initializePhysics();
    }//..

    public void setView(){

        applyKeyControl();

        if(!flying){// get rigid body position

            t = body.getWorldTransform(t);
            t.origin.get(glMatrix);
            pos.x = glMatrix[0];
            pos.y = glMatrix[1];
            pos.z = glMatrix[2];

            if(standing())
                restrictMaxVelocity();

        }else{// set rigid body position
            t = body.getWorldTransform(t);
            t.origin.set(pos.x, pos.y, pos.z);
            body.setWorldTransform(t);
            body.setLinearVelocity(new Vector3f(0, 0, 0));
        }

        if(fog)
            mkFog();

        gl.glRotatef(xrot, 1, 0, 0);  //rotate our camera on teh x-axis (left and right)
        gl.glRotatef(yrot, 0, 1, 0);  //rotate our camera on the y-axis (up and down)
        gl.glTranslatef(-pos.x, -pos.y, -pos.z); //translate the screen to the position of camera
        body.setActivationState(1);
    }//..

    protected void restrictMaxVelocity(){
        //set max run velocity
        //System.out.println("Velovity Restriction");
        body.getLinearVelocity(velocity);
        float curSpeed = velocity.length();
        if(curSpeed > maxSpeed*.56f) {
            velocity.scale(maxSpeed*.56f/curSpeed);
            body.setLinearVelocity(velocity);
        }
    }//..

    public void look(int xIN, int yIN,int center){
            float diffx = xIN - center; //check the difference between the current x and the center position
            float diffy = yIN - center; //check the difference between the current y and the center position
            xrot += diffy * .05; //set the xrot to xrot with the addition of the difference in the y position
            yrot += diffx * .05;// set the xrot to yrot with the addition of the difference in the x position

            // constraints
            xrot = xrot <= -80 ? -80 : xrot;
            xrot = xrot >= 75 ? 75 : xrot;
    }//..

    protected void applyKeyControl(){
        if (keyPressed[w]){
            forward();
        }
        if(keyPressed[s] ){
            backward();
        }
        if(keyPressed[a] ){
            straffLeft();
        }
        if (keyPressed[d]){
            strafeRight();
        }
        if (keyPressed[space]){
                jump();
        }
        if (keyPressed[z]){
            if(flying)
                pos.y-=.25;
        }

        if (keyPressed[shift]){
            maxSpeed=runSpeed;
            flySpeed=runSpeed;
        }else{
            maxSpeed=walkSpeed;
            flySpeed=.25f;
        }
    }//..

    protected void forward(){
        if(flying) {
            setCartesian();
            pos.x += (Math.sin(yrotrad))*flySpeed;
            pos.z -= (Math.cos(yrotrad))*flySpeed;
            pos.y -= (Math.sin(xrotrad)) * flySpeed;
        }else if(standing()){
            body.applyCentralImpulse(getRunVelocity(maxSpeed, 0));
        }
    }//..

    protected void backward(){
        if(flying) {
            setCartesian();
            pos.x -= (Math.sin(yrotrad))*flySpeed*.5;
            pos.z += (Math.cos(yrotrad))*flySpeed*.5;
            pos.y += (Math.sin(xrotrad)) * flySpeed;
        }else if(standing()){
            body.applyCentralImpulse(getRunVelocity(-maxSpeed, 0));
        }
    }//..

    protected void straffLeft(){
        if(flying) {
            setCartesian();
            pos.x -= (Math.cos(yrotrad)) * flySpeed * .25;
            pos.z -= (Math.sin(yrotrad)) * flySpeed * .25;
        }else if(standing()){
            body.applyCentralImpulse(getRunVelocity(maxSpeed, -90));
        }
    }//..

    protected void strafeRight(){
        if(flying) {
            setCartesian();
            pos.x += (Math.cos(yrotrad)) * flySpeed;
            pos.z += (Math.sin(yrotrad)) * flySpeed;
        }else if(standing()){
            body.applyCentralImpulse(getRunVelocity(maxSpeed, 90));
        }
    }//..

    protected void jump(){
        if(flying) {
            pos.y += .25;
        }else if (standing()) {
            body.applyCentralImpulse(new Vector3f(0, 50, 0));
            jumping=true;
        }
    }//..

    protected boolean standing(){
        if(underFoot(new Vector3f(pos.x, pos.y, pos.z))){//directly under
            jumping=false;
            return true;
        }else if(underFoot(getDirection(radius,90))){// under right side
            jumping=false;
            return true;
        }else if(underFoot(getDirection(radius,-90))){// under left side
            jumping=false;
            return true;
        }
        return false;
    }//..

    protected boolean underFoot(Vector3f posFrom){
        float yt = (pos.y - (pos.size*1.2f));
        Vector3f posTo = new Vector3f(pos.x,yt, pos.z);
        //System.out.println(pos.y+" "+yt);
        CollisionWorld.ClosestRayResultCallback rayResult = new CollisionWorld.ClosestRayResultCallback(posFrom,posTo);
        Scene.world.rayTest(posFrom, posTo, rayResult);
        if(rayResult.hasHit()){
            return true;
        }else {
            return false;
        }
    }//..

    protected void setCartesian(){
        yrotrad = (float) (yrot / 180 * Math.PI);
        xrotrad = (float) (xrot / 180 * Math.PI);
    }//..

    public Vector3f getPosition(float step){
        float nx= pos.x,ny= pos.y,nz= pos.z;
        float xrotrad, yrotrad;
        yrotrad = (float) (yrot / 180 * Math.PI);
        xrotrad = (float) (xrot / 180 * Math.PI);
        nx += (Math.sin(yrotrad))*step;
        nz -= (Math.cos(yrotrad))*step;
        ny -= (Math.sin(xrotrad))*step;
        return new Vector3f(nx,ny,nz);
    }//..

    public float[] getPositionf(float step){
        Vector3f cp = getPosition(1);
        float fp[] ={cp.x,cp.y,cp.z};
        return fp;
    }//..

    public Vector3f getVelocity() {
        return velocity;
    }//..
    public float[] getVelocityf(){
        body.getLinearVelocity(velocity);
        float fv[]={velocity.x,velocity.y,velocity.z};
        return fv;
    }//..



    public Vector3f getDirection(float throwSpeed,float angle){
        float pd = (float) (Math.PI/180);
        float nx = (float) (-Math.cos(xrot*pd)*Math.sin(yrot*pd)),
                ny = (float) Math.sin(xrot*pd),
                nz = (float) (-Math.cos(xrot*pd)*Math.cos(yrot*pd));

        float xrotrad, yrotrad;
        yrotrad = (float) (yrot / 180 * Math.PI);
        xrotrad = (float) (xrot / 180 * Math.PI);

        nx += (Math.sin(yrotrad+angle))*throwSpeed;
        nz -= (Math.cos(yrotrad+angle))*throwSpeed;
        ny -= (Math.sin(xrotrad+angle))*throwSpeed;

        return new Vector3f(nx,ny,nz);
    }//..

    public float[] getDirectionf(){
        Vector3f d = getDirection(1,0);
        float fd[] = {d.x,d.y,d.z};
        return fd;
    }//..

    protected Vector3f getRunVelocity(float runspeed,float angle){
         Vector3f constrainedVelovity = getDirection(runspeed,angle);
        constrainedVelovity.y = 0;
        return constrainedVelovity;
    }//..

    public void keyDown(KeyEvent ke){
        keyPressed[ke.getKeyCode()]=true;
    }//..
    public void keyUp(KeyEvent ke){
        keyPressed[ke.getKeyCode()]=false;

        if (ke.getKeyCode() == ctrl){
            flying=!flying;
        }
        if (ke.getKeyCode() == p){
            setPosition(new Point3d(0,5,0));
        }
    }//..

    protected void initializePhysics(){
        MotionState motionState;
        CollisionShape shape = new CapsuleShape(1, pos.size);

        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(pos.x, pos.y, pos.z));
        Vector3f inertia = new Vector3f(0,0,0);
        shape.calculateLocalInertia(pos.mass,inertia);
        shape.setLocalScaling(new Vector3f(.5f,1,.5f));

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(pos.mass,motionState,shape,inertia);
        body = new RigidBody(info);
        body.setFriction(1f);
        //body.setDamping(1f, 1f);
        body.setRestitution(.2f);
        body.setAngularFactor(0);
        Scene.safeAddRigidBody(body);
    }//..

    float fogDistance = 20000;
    public void mkFog(){
        // Set up fog mode
        float fcolor = .5f;
        float[] fogColor = { fcolor, fcolor, fcolor, 1.0f };   // fog color
        gl.glFogfv(gl.GL_FOG_COLOR, fogColor, 0); // set fog color
        gl.glFogf(gl.GL_FOG_DENSITY, .0005f);      // how dense will the fog be
        gl.glHint(gl.GL_FOG_HINT, gl.GL_NICEST);  // fog hint value  GL_DONT_CARE
        gl.glFogf(gl.GL_FOG_START, 1.0f); // fog start depth
        gl.glEnable(gl.GL_FOG);           // enables GL_FOG
        gl.glFogi(gl.GL_FOG_MODE, gl.GL_LINEAR); // Fog Mode
//        if(fogDistance>400){
//            fogDistance -= 5;
//        }else if(fogDistance<60){
//            fogDistance-=.6;
//        }
        gl.glFogf(gl.GL_FOG_END, fogDistance);   // fog end depth
    }//..

    public void setPosition(Point3d newP){
        pos.x = newP.x;pos.y=newP.y;pos.z=newP.z;
        t.origin.set(pos.x, pos.y, pos.z);
        body.setWorldTransform(t);
        body.setLinearVelocity(new Vector3f(0,0,0));
    }//..

}// wrld.Camera
