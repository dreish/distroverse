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
   public ViewerDispatcher( ViewerWindow w )
      {
      super();
      mWindow = w;
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
      // TODO Auto-generated method stub
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
   throws ProxyErrorException
      {
      mWindow.getWorld().addShape( o.getShape(),
                                   o.getId().toLong(),
                                   o.getParentId().toLong(),
                                   o.getMoveSeq() );
      }

   @Override
   protected void dispatchMoveObject( MoveObject o )
   throws ProxyErrorException
      {
      // TODO Auto-generated method stub
      
      }

   @Override
   protected void dispatchDeleteObject( DeleteObject o )
   throws ProxyErrorException
      {
      // TODO Auto-generated method stub
      
      }
   
   private ViewerWindow mWindow;
   private ProxyClientConnection mProxy;
   }
