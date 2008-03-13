/**
 * 
 */
package org.distroverse.viewer;

import org.distroverse.dvtp.Shape;

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
      window.getWorld().addShape( new Shape() );
      }
   
   @Override
   public void close()
      {
      // TODO AboutControllerPipeline.close()
      }
   }
