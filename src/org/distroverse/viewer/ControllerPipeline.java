/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
/**
 *
 */
package org.distroverse.viewer;

import java.io.IOException;
import java.net.URISyntaxException;

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
   getNew( String url, ViewerWindow window )
   throws URISyntaxException, IOException, ClassNotFoundException,
          Exception
      {
      if ( url.matches( "(?i)about:.*" ) )
         return new AboutControllerPipeline( url, window );
      if ( url.matches( "(?i)dvtp:.*" ) )
         return new ProxyControllerPipeline( url, window );
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
