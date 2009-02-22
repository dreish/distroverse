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
package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.ULong;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.Shape;
import org.distroverse.dvtp.WarpSeq;
import org.distroverse.viewer.VUtil;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Provides some handy utility classes and implements the fairly obvious
 * setQueue() method.  Leaves unimplemented: run(), offer().
 * @author dreish
 */
public abstract class ProxyBase implements DvtpProxy
   {
   public ProxyBase()
      {
      super();
      }

   public void setQueue( BlockingQueue< ProxySendable > queue )
      {
      mClientQueue = queue;
      }

   /**
    * Add an object to the client's queue.  In most cases, there is
    * another method in ProxyBase that provides a convenient alternative
    * to calling this one.
    *
    * For now, this is just going to log and keep retrying if it gets
    * interrupted.  This is probably a very bad strategy, but we'll see.
    * @param o
    */
   protected void putQueue( ProxySendable o )
      {
      while ( true )
         {
         try
            {
            mClientQueue.put( o );
            return;
            }
         catch ( InterruptedException e )
            {
            Log.p( "mClientQueue.put() interrupted:", Log.UNHANDLED,
                   0 );
            Log.p( e, Log.UNHANDLED, 0 );
            }
         }
      }

   protected void addObject( Shape s, ULong id, ULong pid,
                             MoveSeq m )
      {
      putQueue( new AddObject( true, s, id, pid, m, new WarpSeq() ) );
      }

   protected void addObject( Shape s, long id,
                             float x, float y, float z )
      {
      addObject( s, new ULong( id ), new ULong( 0 ),
                 VUtil.simpleMove( new Vector3f( x, y, z ),
                                   new Quaternion() ) );
      }


   private BlockingQueue< ProxySendable > mClientQueue;
   }