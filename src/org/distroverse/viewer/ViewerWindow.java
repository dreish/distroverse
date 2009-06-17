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
package org.distroverse.viewer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.distroverse.core.Log;
import org.distroverse.viewer.gui.DvWindow;

/**
 * @author dreish
 */
public class ViewerWindow
   {
   public ViewerWindow()
      {
      mWindow = new DvWindow();
      final DvWindow window_copy = mWindow;
      mWindowRunner = new Thread( new Runnable()
                                  {
                                  public void run()
                                  // BaseGame has a no-return start()
                                  // method.  Boo.
                                     {  window_copy.start();  }
                                  } );
      mWindowRunner.start();
//      final DebugGameState debug = new DebugGameState();
//      GameStateManager.getInstance().attachChild( debug );
//      debug.setActive( true );
      mGui = new ViewerGui( mWindow );
      mWorld = new WorldGraph( mGui.getWorldRootNode() );
      }

   /**
    * Requests that this viewer go to a given URL by either getting a
    * new envoy, or sending a SetUrl object to the existing envoy.
    * @param url - the DVTP URL to go to
    * @throws URISyntaxException - goes without saying
    * @throws IOException - general I/O problems, typically network
    * @throws ClassNotFoundException - if the envoy cannot be loaded
    */
   public void requestUrl( String url )
      {
      try
         {
         if ( mPipeline == null  ||  ! mPipeline.handlesUrl( url ) )
            newPipelineUrl( url );
         else
            mPipeline.requestUrl( url );
         setDisplayedUrl( url );
         }
      catch ( URISyntaxException e )
         {
         Log.p( "Bad URI syntax: " + url, Log.CLIENT, 0 );
         Log.p( e, Log.CLIENT, 0 );
         }
      catch ( IOException e )
         {
         Log.p( "I/O error from: " + url, Log.NET, 10 );
         Log.p( e, Log.NET, 10 );
         }
      catch ( Exception e )
         {
         Log.p( "Unloadable envoy from: " + url, Log.DVTP, 5 );
         Log.p( e, Log.DVTP, 5 );
         }
      }

   public void setDisplayedUrl( String url )
      {
      mGui.getLocationBar().setText( url );
      }

   private void newPipelineUrl( String url )
   throws Exception
      {
      if ( mPipeline != null )
         mPipeline.close();
      mPipeline = ControllerPipeline.getNew( url, this );
      }

   public WorldGraph getWorld()  {  return mWorld;  }

   private DvWindow           mWindow;
   private ControllerPipeline mPipeline;
   private ViewerGui          mGui;
   private WorldGraph         mWorld;
   private Thread             mWindowRunner;
   }
