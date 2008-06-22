/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
/**
 * 
 */
package org.distroverse.viewer;

import java.io.IOException;
import java.net.URISyntaxException;

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
public abstract class ControllerPipeline
   {
   /**
    * Returns a new subclass of ControllerPipeline, appropriate for the
    * given URL, targeting the given game engine and window, and
    * possibly interacting with the given proxy connection.
    * @param url
    * @param proxy
    * @param game
    * @param window
    * @return
    * @throws IOException 
    * @throws URISyntaxException 
    * @throws ClassNotFoundException 
    */
   public static ControllerPipeline
   getNew( String url, ProxyClientConnection proxy, StandardGame game,
           ViewerWindow window ) 
   throws URISyntaxException, IOException, ClassNotFoundException,
          Exception
      {
      if ( url.matches( "(?i)about:.*" ) )
         return new AboutControllerPipeline( url, game, window );
      if ( url.matches( "(?i)dvtp:.*" ) )
         return new ProxyControllerPipeline( url, game, window, proxy );
      return null;
      }

   /**
    * Stops reading from the proxy, and removes everything written to
    * the game (window).
    */
   public abstract void close();

   /**
    * A typical implementation would find out whether it needs a new
    * proxy, and either send a SetUrl object to its proxy or get a new
    * one.
    * @param url
    * @throws IOException 
    * @throws URISyntaxException 
    * @throws ClassNotFoundException
    * @throws Exception
    */
   @SuppressWarnings("unused")
   public void requestUrl( String url )
   throws URISyntaxException, IOException, ClassNotFoundException,
   Exception
      {
      throw new RuntimeException( "This controller pipeline does not"
                                  + " handle requestUrl()s" );
      }

   /**
    * Return true if this ControllerPipeline handles, or is attached to
    * an object that handles, the given URL.  Any class that overrides
    * this method with one that can return true for a given URL should
    * also handle setUrl() calls with such a URL.
    * @param url - URL to handle
    * @return
    */
   public boolean handlesUrl( String url )
      {
      return false;
      }
   }
