package wrld;

import com.bulletphysics.util.ObjectArrayList;
import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

/**
 * Created by Kevin on 12/25/13.
 */


public class ObjectLoader {


    GL2 gl;

    ObjectLoader(GL2 gl) {
       this.gl = gl;
    }//..

    public int LoadOBJ(String fileLocation,String filename,ObjectArrayList<Vector3f> points,Vector<Material> material ){


        Vector<String> coord= new Vector<String>();
        Vector<Coordinate> vertex = new Vector<Coordinate>();
        Vector<Face> faces = new Vector<Face>();
        Vector<Coordinate> normal = new Vector<Coordinate>();
        Vector<TexCoord> textureCoord = new Vector<TexCoord>();

        if(material==null)
            material=new Vector<Material>();

        boolean isNormal = false, isTexture = false;

        int curmat=0;

        String delims = "[ /]+";
        String line;


        BufferedReader br;

        try {
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileLocation+filename), "UTF-8"));
            while((line = br.readLine()) != null) {// read entire .obj file into ram
                if(line.length() > 0)
                    coord.add(line);
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error Loading: "+fileLocation+filename);
        }


        for(int i=0; i<coord.size();i++){// loop through all lines of .obg in ram

            line = coord.get(i);

            if(line.charAt(0) == 'v' && line.charAt(1) == ' '){
                // if it is a vector add to vectors array
                String[] token = line.split(delims);
                float tmpx = Float.parseFloat(token[1]),
                        tmpy = Float.parseFloat(token[2]),
                        tmpz = Float.parseFloat(token[3]);
                vertex.add(new Coordinate(tmpx,tmpy,tmpz));

            }else if(line.charAt(0) == 'v' && line.charAt(1) == 'n'){
                // if normal add to noramas array
                String[] token = line.split(delims);
                float tmpx = Float.parseFloat(token[1]),
                        tmpy = Float.parseFloat(token[2]),
                        tmpz = Float.parseFloat(token[3]);
                normal.add(new Coordinate(tmpx,tmpy,tmpz));
                isNormal = true;
            }else if(line.charAt(0) == 'f'){
                // if face add to normals face
                String[] token = line.split(delims); // parse line based on delimiters;

                if(token.length == 13){//if quad and texcoord
                    int     a = Integer.parseInt(token[1]),
                            b =Integer.parseInt(token[3]),//face number
                            c = Integer.parseInt(token[4]),
                            d =Integer.parseInt(token[7]),
                            e =Integer.parseInt(token[10]),
                            t1 =Integer.parseInt(token[2]),
                            t2 =Integer.parseInt(token[5]),
                            t3 =Integer.parseInt(token[8]),
                            t4 =Integer.parseInt(token[11]);
                    faces.add(new Face(b,a,c,d,e,t1,t2,t3,t4,curmat));
                }else if(token.length == 9){ //if quad and no texcoord
                    int     a = Integer.parseInt(token[1]),
                            b =Integer.parseInt(token[2]), //face number
                            c = Integer.parseInt(token[3]),
                            d =Integer.parseInt(token[5]),
                            e = Integer.parseInt(token[7]);
                    faces.add(new Face(b,a,c,d,e,0,0,0,0,curmat));
                }else if(token.length == 7){ //if triangle and no texcoord
                    int     a = Integer.parseInt(token[1]),
                            b =Integer.parseInt(token[2]), //face number
                            c = Integer.parseInt(token[3]),
                            d =Integer.parseInt(token[5]);
                    faces.add(new Face(b,a,c,d,0,0,0,curmat));
                }else if(token.length == 10){//if triangle vertex and texcoord
                    int     a = Integer.parseInt(token[1]),
                            b =Integer.parseInt(token[3]), //face number
                            c = Integer.parseInt(token[4]),
                            d =Integer.parseInt(token[7]),
                            t1 =Integer.parseInt(token[2]),
                            t2 =Integer.parseInt(token[5]),
                            t3 =Integer.parseInt(token[8]);
                    faces.add(new Face(b,a,c,d,t1,t2,t3,curmat));
                }

            }else if(line.charAt(0) == 'u' && line.charAt(1) == 's' && line.charAt(2) == 'e'){// if use material
                if(material!=null)
                if(material.size()!=0) {
                    String[] token = line.split(delims);

                    String tmp = token[1];

                    for (int j = 0; j < material.size(); j++) {

                        if (material.get(j).name.equals(tmp)) {
                            curmat = j;
                            break;
                        }
                    }
                }
            }else if(line.charAt(0) == 'm' && line.charAt(1) == 't' && line.charAt(2) == 'l' && line.charAt(3) == 'l'){// if use mtllib
                if(material.size()==0) {
                    String[] token = line.split(delims);
                    //System.out.println("Loading Materials: "+filename);

                    String mtlLine;
                    String mtlFileLocation = fileLocation + token[1];
                    BufferedReader mtlIn;

                    Vector<String> temp = new Vector<String>();


                    try {
                        mtlIn = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(mtlFileLocation)));
                        while ((mtlLine = mtlIn.readLine()) != null) {// read entire .mtl file into ram
                            if (mtlLine.length() > 0)
                                temp.add(mtlLine);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error Loading: " + mtlFileLocation);
                    }

                    String matName = " ", texFilename;
                    float
                            amb[] = new float[]{0, 0, 0},
                            dif[] = new float[]{0, 0, 0},
                            spec[] = new float[]{0, 0, 0},
                            alpha = 0, ns = 0, ni = 0;
                    int illum = 0;
                    Texture texture = null;
                    boolean isMat = false;
                    texFilename = "n";

                    //begin iterating through .mtl
                    for (int m = 0; m < temp.size(); m++) {
                        if (temp.get(m).charAt(0) == '#') {// skip comments
                            //continue;
                        } else if (temp.get(m).charAt(0) == 'n' && temp.get(m).charAt(1) == 'e' && temp.get(m).charAt(2) == 'w') {
                            if (isMat) {
                                if (!texFilename.equals("n")) {
                                    material.add(new Material(matName, alpha, ns, ni, dif, amb, spec, illum, texture));
                                    texFilename = "n";
                                } else {
                                    //System.out.println("New Material: "+matName+" "+alpha+" "+ns+" "+ni+" "+dif[0]+" "+amb[0]+" "+spec[0]+" "+illum);
                                    material.add(new Material(matName, alpha, ns, ni, dif, amb, spec, illum, null));
                                }
                            }
                            isMat = false;
                            String nToken[] = temp.get(m).split(delims);
                            matName = nToken[1];

                        } else if (temp.get(m).charAt(0) == 'N' && temp.get(m).charAt(1) == 's') {
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            ns = Float.parseFloat(nToken[1]);

                        } else if (temp.get(m).charAt(0) == 'K' && temp.get(m).charAt(1) == 'a') {// read Ka ambient float vector
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            amb[0] = Float.parseFloat(nToken[1]);
                            amb[1] = Float.parseFloat(nToken[2]);
                            amb[2] = Float.parseFloat(nToken[3]);
                        } else if (temp.get(m).charAt(0) == 'K' && temp.get(m).charAt(1) == 'd') {// read Ka diffuse float vector
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            dif[0] = Float.parseFloat(nToken[1]);
                            dif[1] = Float.parseFloat(nToken[2]);
                            dif[2] = Float.parseFloat(nToken[3]);
                        } else if (temp.get(m).charAt(0) == 'K' && temp.get(m).charAt(1) == 's') {// read Ka specular float vector
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            spec[0] = Float.parseFloat(nToken[1]);
                            spec[1] = Float.parseFloat(nToken[2]);
                            spec[2] = Float.parseFloat(nToken[3]);
                        } else if (temp.get(m).charAt(0) == 'N' && temp.get(m).charAt(1) == 'i') {// read Ni ?
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            ni = Float.parseFloat(nToken[1]);
                        } else if (temp.get(m).charAt(0) == 'd' && temp.get(m).charAt(1) == ' ') {// read alpha
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            alpha = Float.parseFloat(nToken[1]);
                        } else if (temp.get(m).charAt(0) == 'i' && temp.get(m).charAt(1) == 'l' && temp.get(m).charAt(2) == 'l') {// read illum
                            isMat = true;
                            String nToken[] = temp.get(m).split(delims);
                            illum = Integer.parseInt(nToken[1]);
                        } else if (temp.get(m).charAt(0) == 'm' && temp.get(m).charAt(1) == 'a' && temp.get(m).charAt(2) == 'p') {// read map_Kd
                            isMat = true;
                            String spaceDelim = "\\s+";
                            String nToken[] = temp.get(m).split(spaceDelim);
                            texFilename = nToken[1];
                            if ((texture = Utils.loadTexture(getClass(),gl,fileLocation + texFilename)) != null) {
                            } else {
                                texFilename = "n";
                            }


                        }
                    }// end for .mtl
                    //System.out.println("Materials Loaded");
                    if (isMat) {
                        if (!texFilename.equals("n")) {
                            material.add(new Material(matName, alpha, ns, ni, dif, amb, spec, illum, texture));
                        } else {
                            material.add(new Material(matName, alpha, ns, ni, dif, amb, spec, illum, null));
                        }
                    }
                }// end if(materials.size()==0)

            }else  if(line.charAt(0) == 'v' && line.charAt(1) == 't'){
                String[] token = line.split(delims);
                textureCoord.add(new TexCoord(Float.parseFloat(token[1]),1 - Float.parseFloat(token[2])));
                isTexture = true;

            }// end check what line is describing// endif mtllib

        }// end looping through .obj
        // System.out.println("Object File Loaded");


        //draw
        int num = gl.glGenLists(1);
        gl.glNewList(num, gl.GL_COMPILE);
        int last = -1;
        for(int i=0; i<faces.size();i++){
            if(last != faces.get(i).mat ){
                    float diffuse[] = {material.get(faces.get(i).mat).dif[0], material.get(faces.get(i).mat).dif[1], material.get(faces.get(i).mat).dif[2], 1};
                    float ambient[] = {material.get(faces.get(i).mat).amb[0], material.get(faces.get(i).mat).amb[1], material.get(faces.get(i).mat).amb[2], 1};
                    float specular[] = {material.get(faces.get(i).mat).spec[0], material.get(faces.get(i).mat).spec[1], material.get(faces.get(i).mat).spec[2], 1};

                    gl.glMaterialfv(gl.GL_FRONT, gl.GL_DIFFUSE, mkFloatBuffer(diffuse));
                    gl.glMaterialfv(gl.GL_FRONT, gl.GL_AMBIENT, mkFloatBuffer(ambient));
                    gl.glMaterialfv(gl.GL_FRONT, gl.GL_SPECULAR, mkFloatBuffer(specular));
                    gl.glMaterialf(gl.GL_FRONT, gl.GL_SHININESS, material.get(faces.get(i).mat).na);
                    last = faces.get(i).mat;

                    if (material.get(faces.get(i).mat).texture == null) {
                        gl.glDisable(gl.GL_TEXTURE_2D);
                    } else {
                        material.get(faces.get(i).mat).texture.enable(gl);
                        material.get(faces.get(i).mat).texture.bind(gl);
                    }
            }
            if(faces.get(i).four){
                gl.glBegin(gl.GL_QUADS);
                if(isTexture ){
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[0]-1).u,textureCoord.get(faces.get(i).texCoord[0]-1).v);
                }
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                gl.glVertex3f(vertex.get(faces.get(i).faces[0] - 1).x, vertex.get(faces.get(i).faces[0] - 1).y, vertex.get(faces.get(i).faces[0] - 1).z);

                if(isTexture ){
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[1]-1).u,textureCoord.get(faces.get(i).texCoord[1]-1).v);
                }
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                gl.glVertex3f(vertex.get(faces.get(i).faces[1] - 1).x, vertex.get(faces.get(i).faces[1] - 1).y, vertex.get(faces.get(i).faces[1] - 1).z);

                if(isTexture ){
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[2]-1).u,textureCoord.get(faces.get(i).texCoord[2]-1).v);
                }
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                gl.glVertex3f(vertex.get(faces.get(i).faces[2] - 1).x, vertex.get(faces.get(i).faces[2] - 1).y, vertex.get(faces.get(i).faces[2] - 1).z);

                if(isTexture  ){
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[3]-1).u,textureCoord.get(faces.get(i).texCoord[3]-1).v);
                }
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                gl.glVertex3f(vertex.get(faces.get(i).faces[3] - 1).x, vertex.get(faces.get(i).faces[3] - 1).y, vertex.get(faces.get(i).faces[3] - 1).z);
                gl.glEnd();
            }else{
                boolean needPoints;
                needPoints = points!=null ? true : false;
                gl.glBegin(gl.GL_TRIANGLES);

                if(isTexture )
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[0]-1).u,textureCoord.get(faces.get(i).texCoord[0]-1).v);
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum - 1).x, normal.get(faces.get(i).faceNum - 1).y, normal.get(faces.get(i).faceNum - 1).z);
                if(needPoints)
                    points.add(new Vector3f(vertex.get(faces.get(i).faces[0] - 1).x, vertex.get(faces.get(i).faces[0] - 1).y, vertex.get(faces.get(i).faces[0] - 1).z));
                gl.glVertex3f(vertex.get(faces.get(i).faces[0] - 1).x, vertex.get(faces.get(i).faces[0] - 1).y, vertex.get(faces.get(i).faces[0] - 1).z);

                if(isTexture )
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[1]-1).u,textureCoord.get(faces.get(i).texCoord[1]-1).v);
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                if(needPoints)
                    points.add(new Vector3f(vertex.get(faces.get(i).faces[1] - 1).x, vertex.get(faces.get(i).faces[1] - 1).y, vertex.get(faces.get(i).faces[1] - 1).z));
                gl.glVertex3f(vertex.get(faces.get(i).faces[1] - 1).x, vertex.get(faces.get(i).faces[1] - 1).y, vertex.get(faces.get(i).faces[1] - 1).z);

                if(isTexture)
                    gl.glTexCoord2f(textureCoord.get(faces.get(i).texCoord[2]-1).u,textureCoord.get(faces.get(i).texCoord[2]-1).v);
                if(isNormal)
                    gl.glNormal3f(normal.get(faces.get(i).faceNum-1).x,normal.get(faces.get(i).faceNum-1).y,normal.get(faces.get(i).faceNum-1).z);
                if(needPoints)
                    points.add(new Vector3f(vertex.get(faces.get(i).faces[2] - 1).x, vertex.get(faces.get(i).faces[2] - 1).y, vertex.get(faces.get(i).faces[2] - 1).z));
                gl.glVertex3f(vertex.get(faces.get(i).faces[2] - 1).x, vertex.get(faces.get(i).faces[2] - 1).y, vertex.get(faces.get(i).faces[2] - 1).z);

                gl.glEnd();
            }
        }

        gl.glEndList();
        return num;
    }//..

    public void loadAnimation(Vector<Integer> frames,String fileLocation,String filename, int num){

        Vector<Material> material = new Vector<Material>();
        //System.out.println("Loading Object: "+filename);
        String tmp ="";

        for (int i=1;i<num;i++){
            if(i<10){
                tmp = "_00000"+i;
            }else if(i<100){
                tmp = "_0000"+i;
            }else if(i<1000){
                tmp = "_000"+i;
            }else if(i<10000){
                tmp = "_00"+i;
            }else if(i<1000000){
                tmp = "_0"+i;
            }else if(i<100000){
                tmp = "_"+i;
            }
            String fileSub = filename+tmp+".obj";
            int obj = LoadOBJ(fileLocation,fileSub,null,material);
            frames.add(obj);
        }
    }//..

    private FloatBuffer mkFloatBuffer(float vertices[]){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        FloatBuffer fb = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        fb.put(vertices);    // add the coordinates to the FloatBuffer
        fb.position(0);      // set the buffer to read the first coordinate
        return fb;
    }//..

}// end Class ObjectLoader


class Material{

    String name;
    float alpha, na,ni;
    float dif[] = new float[3],
          amb[] = new float[3],
          spec[] = new float[3];
    int illum;
    Texture texture;

    Material(String name, float alpha, float na, float ni, float[] dif,float[] amb,float[] spec, int illum, Texture texture){
        this.name=name;
        this.alpha=alpha;
        this.na=na;
        this.ni = ni;
        this.dif = dif;
        this.amb=amb;
        this.spec=spec;
        this.illum = illum;
        this.texture = texture;
    }//..
}// end Material


class TexCoord{

    float u,v;
    TexCoord(float u, float v){
        this.u = u;
        this.v = v;
    }//..

}// end TexCoord


class Coordinate{
    float x,y,z;

    Coordinate(float a, float b, float c){
        x=a;y=b;z=c;
    }//..
}// end Coordinate

class Face{
    int faceNum, faces[] = new int[4];
    boolean four;
    int texCoord[] = new int[4];
    int mat;

    Face(int faceN, int f1,int f2, int f3,int f4, int t1, int t2, int t3,int t4, int m){
        mat =m;
        texCoord[0] = t1;
        texCoord[1] = t2;
        texCoord[2] = t3;
        texCoord[3] = t4;
        faceNum = faceN;
        faces[0]=f1;
        faces[1]=f2;
        faces[2]=f3;
        faces[3]=f4;
        mat = m;
        four = true;

    }//..
    Face(int faceN, int f1,int f2, int f3, int t1, int t2, int t3, int m){
        mat =m;
        texCoord[0] = t1;
        texCoord[1] = t2;
        texCoord[2] = t3;
        faces[3]=0;
        faceNum = faceN;
        faces[0]=f1;
        faces[1]=f2;
        faces[2]=f3;
        mat = m;
        four = false;
    }//..
}// end Face