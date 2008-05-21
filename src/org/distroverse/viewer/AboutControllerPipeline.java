/**
 * 
 */
package org.distroverse.viewer;

import java.util.ArrayList;

import javax.vecmath.Point3d;

import org.distroverse.distroplane.lib.BallFactory;
import org.distroverse.dvtp.Shape;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
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
                    .setNumRows( 30 )
                    .setEquatorialRadius( 2 )
                    .generate();
      wg.addShape( s, 1L, 0L,
                   VUtil.simpleMove( new Vector3f( 0, 0, -100 ), 
                                     new Quaternion( 1, 0, 0, 0 ) ) );
      Shape simple = simpleShape();
      wg.addShape( simple, 2L, 0L,
                   VUtil.simpleMove( new Vector3f( 0, 0, 0 ), 
                                     new Quaternion( 1, 0, 0, 0 ) ) );
      }

   // Returns a single right triangle, for testing.
   private Shape simpleShape()
      {
      ArrayList< Point3d > alp = new ArrayList< Point3d >();
      alp.add( new Point3d( 0, 0, 1 ) );
      alp.add( new Point3d( 1, 0, 1 ) );
      alp.add( new Point3d( 0, 1, 1 ) );
      int[] vc = { 3 };
      
      return new Shape( alp, vc, 1 );
      }
   
   @Override
   public void close()
      {
      // Don't need to do anything; there's no state to save.
      }
   }
