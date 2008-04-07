/**
 * 
 */
package org.distroverse.viewer;

import java.util.ArrayList;

import javax.vecmath.Point3d;

import org.distroverse.distroplane.lib.BallFactory;
import org.distroverse.dvtp.Shape;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;
import com.jmex.game.StandardGame;

/**
 * This controller pipeline knows how to generate about:* views, which
 * do not use a proxy.
 * @author dreish
 */
public class AboutControllerPipeline extends ControllerPipeline
   {
   public AboutControllerPipeline( String url, StandardGame game,
                                   ViewerWindow window )
      {
      WorldGraph wg = window.getWorld();
      wg.clear();
      // TODO: load from a file instead of using distroplane.lib.
//      Shape s = new BallFactory()
//                    .setNumRows( 6 )
//                    .setEquatorialRadius( 40 )
//                    .generate();
//      wg.addShape( s, "octey", null, 
//                   new Vector3f( 0, 0, -100 ), 
//                   new Quaternion( 1, 0, 0, 0 ) );
      TriMesh shady_square = squareInit();
      wg.tmpAddTM( shady_square, "shady_square", null,
                   new Vector3f( 0, 0, 0 ),
                   new Quaternion( 1, 0, 0, 0 ) );
//      Shape simple = simpleShape();
//      wg.addShape( simple, "simple", null, 
//                   new Vector3f( 0, 0, 0 ), 
//                   new Quaternion( 1, 0, 0, 0 ) );
      }

   // Returns a single right triangle, for testing.
   private Shape simpleShape()
      {
      ArrayList< Point3d > alp = new ArrayList< Point3d >();
      alp.add( new Point3d( 0, 0, -1 ) );
      alp.add( new Point3d( 1, 0, -1 ) );
      alp.add( new Point3d( 0, 1, -1 ) );
      int[] vc = { 3 };
      
      return new Shape( alp, vc );
      }
   
   // This code was yanked from HelloTriMesh.java, which is why it's so
   // hideous.
   private TriMesh squareInit()
      {
      // Vertex positions for the mesh
      Vector3f[] vertexes={
          new Vector3f(0,0,1),
          new Vector3f(1,0,1),
          new Vector3f(0,1,1),
          new Vector3f(1,1,1)
      };

      // Normal directions for each vertex position
      Vector3f[] normals={
          new Vector3f(0,0,1),
          new Vector3f(1,0,1),
          new Vector3f(0,1,1),
          new Vector3f(1,1,1)
      };

      // Color for each vertex position
      ColorRGBA[] colors={
          new ColorRGBA(0.5f,0.5f,0.5f,0.5f),
          new ColorRGBA(0.5f,0.5f,0.5f,0.5f),
          new ColorRGBA(0.5f,0.5f,0.5f,0.5f),
          new ColorRGBA(0.5f,0.5f,0.5f,0.5f),
      };

      // Texture Coordinates for each position
      Vector2f[] texCoords={
          new Vector2f(0,0),
          new Vector2f(1,0),
          new Vector2f(0,1),
          new Vector2f(1,1)
      };

      // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
      int[] indexes={
          0,1,2 //,1,2,3
      };

      // TriMesh is what most of what is drawn in jME actually is
      TriMesh m=new TriMesh("My Mesh",
                    BufferUtils.createFloatBuffer(vertexes),
                    BufferUtils.createFloatBuffer(normals),
                    BufferUtils.createFloatBuffer(colors), 
//                    BufferUtils.createFloatBuffer(texCoords), 
                    BufferUtils.createFloatBuffer(vertexes),
                    BufferUtils.createIntBuffer(indexes));

      // Create a bounds
      m.setModelBound(new BoundingBox());
      m.updateModelBound();
      
      return m;
      }

   @Override
   public void close()
      {
      // TODO AboutControllerPipeline.close()
      }
   }
