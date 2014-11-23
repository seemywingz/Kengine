package wrld;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Created by Kevin on 12/19/13.
 * Stores created textures
 */
public final class Textures {

    public static Texture
            sky = Utils.loadTexture(Scene.gl,"/tex/sky.jpg"),
            grass = Utils.loadTexture(Scene.gl, "/tex/ground.png"),
            cannonBall = Utils.loadTexture(Scene.gl,"/tex/cannonball.jpg"),
            basketBall = Utils.loadTexture(Scene.gl, "/tex/basketball.jpg"),
            box = Utils.loadTexture(Scene.gl, "/tex/box.jpg");



}// end Class Textures
