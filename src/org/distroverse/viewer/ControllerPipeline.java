/**
 * 
 */
package org.distroverse.viewer;

import com.jmex.game.StandardGame;

/**
 * A subclass of ControllerPipeline receives objects from a
 * ProxyClientConnection, responds to them appropriately, which in most
 * cases means some operation on a StandardGame (a jME window). It also
 * receives user events and either forwards them to the proxy, or to the
 * window.
 * 
 * @author dreish
 */
public class ControllerPipeline
   {
   /**
    * 
    */
   protected ControllerPipeline()
      {
      // TODO Auto-generated constructor stub
      }

   /**
    * Returns a new subclass of ControllerPipeline, appropriate for the
    * given URL, targeting the given game engine and window, and
    * possibly interacting with the given proxy connection.
    * @param url
    * @param proxy
    * @param game
    * @param window
    * @return
    */
   public static ControllerPipeline
   getNew( String url, ProxyClientConnection proxy, StandardGame game,
           ViewerWindow window )
      {
      if ( url.matches( "(?i)about:" ) )
         return new AboutControllerPipeline( url, game, window );
      return null;
      }

   /**
    * Stops reading from the proxy, and removes everything written to
    * the game (window).
    */
   public void close()
      {
      // TODO Auto-generated method stub
      }
   }
