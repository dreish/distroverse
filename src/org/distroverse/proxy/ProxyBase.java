/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.Shape;
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
   
   protected void addObject( Shape s, CompactUlong id, CompactUlong pid,
                             MoveSeq m )
      {
      putQueue( new AddObject( s, id, pid, m ) );
      }

   protected void addObject( Shape s, long id,
                             float x, float y, float z )
      {
      addObject( s, new CompactUlong( id ), new CompactUlong( 0 ),
                 VUtil.simpleMove( new Vector3f( x, y, z ),
                                   new Quaternion() ) );
      }

   
   private BlockingQueue< ProxySendable > mClientQueue;
   }