/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer;

import org.distroverse.core.Log;
import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.ClientDispatcher;
import org.distroverse.dvtp.DeleteObject;
import org.distroverse.dvtp.DisplayUrl;
import org.distroverse.dvtp.MoveObject;
import org.distroverse.dvtp.RedirectUrl;

/**
 * An implementation of the ClientDispatcher abstraction designed to
 * work with the Viewer client.
 * @author dreish
 */
public class ViewerDispatcher extends ClientDispatcher
   {
   public ViewerDispatcher( ViewerWindow w, ProxyClientConnection p )
      {
      super();
      mWindow = w;
      mProxy  = p;
      }

   @Override
   protected void dispatchDisplayUrl( DisplayUrl o )
   throws ProxyErrorException
      {
      String url = o.getUrl();

      if ( mProxy.handlesUrl( url ) )
         mWindow.setDisplayedUrl( url );
      else
         throw new ProxyErrorException( "Proxy tried to display a URL"
                           + " that does not belong to it: " + url );
      }

   @Override
   protected void dispatchRedirectUrl( RedirectUrl o )
   throws ProxyErrorException
      {
      String url = o.getUrl();

      if ( mProxy.handlesUrl( url ) )
         throw new ProxyErrorException( "Proxy tried to redirect to"
                          + "a URL that it handles: " + url );

      try
         {
         mWindow.requestUrl( url );
         }
      catch ( Exception e )
         {
         Log.p( "Could not redirect to requested url "
                + url + ":", Log.NET, 0 );
         Log.p( e, Log.NET, 0 );
         }
      }

   @Override
   protected void dispatchAddObject( AddObject o )
      {
      mWindow.getWorld().addShape( o.getShape(),
                                   o.getId().toLong(),
                                   o.getParentId().toLong(),
                                   o.getMoveSeq() );
      }

   @Override
   protected void dispatchMoveObject( MoveObject o )
      {
      mWindow.getWorld().setMoveSeq( o.getMoveSeq(), o.getId() );
      }

   @Override
   protected void dispatchDeleteObject( DeleteObject o )
      {
      mWindow.getWorld().deleteShape( o.getId() );
      }

   private ViewerWindow mWindow;
   private ProxyClientConnection mProxy;
   }
