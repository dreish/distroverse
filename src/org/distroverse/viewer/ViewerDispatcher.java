package org.distroverse.viewer;

import org.distroverse.dvtp.ClientDispatcher;
import org.distroverse.dvtp.SetUrl;

public class ViewerDispatcher extends ClientDispatcher
   {
   public ViewerDispatcher( ViewerWindow w )
      {
      super();
      mWindow = w;
      }
   
   @Override
   protected void dispatchSetUrl( SetUrl o )
      {
      mWindow.setUrl( o.getUrl() );
      }
   
   private ViewerWindow mWindow;
   }
