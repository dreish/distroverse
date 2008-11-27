/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
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
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
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
   public ViewerDispatcher( ViewerWindow w, ControllerPipeline p )
      {
      super();
      mWindow   = w;
      mPipeline = p;
      }

   @Override
   protected void dispatchDisplayUrl( DisplayUrl o )
   throws ProxyErrorException
      {
      String url = o.getUrl();

      if ( mPipeline.handlesUrl( url ) )
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

      if ( mPipeline.handlesUrl( url ) )
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

   private ViewerWindow       mWindow;
   private ControllerPipeline mPipeline;
   }
