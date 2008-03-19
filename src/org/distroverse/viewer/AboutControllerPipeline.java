/**
 * 
 */
package org.distroverse.viewer;

import org.distroverse.distroplane.lib.BallFactory;

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
      BallFactory bf = new BallFactory();
      bf.setNumRows( 3 );
      wg.addShape( bf.generate(), "octey", null, 
                   new Vector3f( 0, 0, 0 ), 
                   new Quaternion( 1, 0, 0, 0 ) );
      }
   
   @Override
   public void close()
      {
      // TODO AboutControllerPipeline.close()
      }
   }
