package wrld;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Created by Kevin on 12/19/13.
 * Stores created textures
 */
public final class Textures {

    public static Texture
            sky = Utils.loadTexture("/tex/sky.jpg"),
            grass = Utils.loadTexture("/tex/ground.png"),
            cannonBall = Utils.loadTexture("/tex/cannonball.jpg"),
            basketBall = Utils.loadTexture("/tex/basketball.jpg"),
            box = Utils.loadTexture("/tex/box.jpg"),
            wall = Utils.loadTexture("/tex/wall.jpg");
}// end Class Textures
