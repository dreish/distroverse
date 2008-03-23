/**
 * 
 */
package org.distroverse.viewer;

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
      Shape s = new BallFactory()
                    .setNumRows( 6 )
                    .setEquatorialRadius( 40 )
                    .generate();
      wg.addShape( s, "octey", null, 
                   new Vector3f( 0, 0, -100 ), 
                   new Quaternion( 1, 0, 0, 0 ) );
      TriMesh shady_square = squareInit();
      wg.tmpAddTM( shady_square, "shady_square", null,
                   new Vector3f( 0, 0, -100 ),
                   new Quaternion( 1, 0, 0, 0 ) );
      }
   
   // This code was yanked from HelloTriMesh.java, which is why it's so
   // hideous.
   private TriMesh squareInit()
      {
      // TriMesh is what most of what is drawn in jME actually is
      TriMesh m=new TriMesh("My Mesh");

      // Vertex positions for the mesh
      Vector3f[] vertexes={
          new Vector3f(0,0,0),
          new Vector3f(1,0,0),
          new Vector3f(0,1,0),
          new Vector3f(1,1,0)
      };

      // Normal directions for each vertex position
      Vector3f[] normals={
          new Vector3f(0,0,1),
          new Vector3f(0,0,1),
          new Vector3f(0,0,1),
          new Vector3f(0,0,1)
      };

      // Color for each vertex position
      ColorRGBA[] colors={
          new ColorRGBA(1,0,0,1),
          new ColorRGBA(1,0,0,1),
          new ColorRGBA(0,1,0,1),
          new ColorRGBA(0,1,0,1)
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
          0,1,2,1,2,3
      };

      // Feed the information to the TriMesh
      m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
              BufferUtils.createFloatBuffer(colors), BufferUtils.createFloatBuffer(texCoords), BufferUtils.createIntBuffer(indexes));

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
