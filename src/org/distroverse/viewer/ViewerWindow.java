/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
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
    * new proxy, or sending a SetUrl object to the existing proxy.
    * @param url - the DVTP URL to go to
    * @throws URISyntaxException - goes without saying
    * @throws IOException - general I/O problems, typically network
    * @throws ClassNotFoundException - if the proxy cannot be loaded
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
         Log.p( "Unloadable proxy from: " + url, Log.DVTP, 5 );
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
