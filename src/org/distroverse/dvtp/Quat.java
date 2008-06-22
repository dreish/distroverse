/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jme.math.Quaternion;

public class Quat implements DvtpExternalizable
   {
   public Quat()
      {
      super();
      }
   
   public Quat( Quaternion q )
      {
      mQuat = q;
      }
   
   public Quaternion asQuaternion()
      {  return mQuat;  }
   
   public int getClassNumber()
      {  return 16;  }

   public static Quaternion externalAsQuaternion( InputStream in )
   throws IOException
      {
      return new Quaternion( Flo.externalAsFloat( in ),
                             Flo.externalAsFloat( in ),
                             Flo.externalAsFloat( in ),
                             Flo.externalAsFloat( in ) );
      }
   
   public static void quaternionAsExternal( OutputStream out,
                                            Quaternion q )
   throws IOException
      {
      Flo.floatAsExternal( out, q.w );
      Flo.floatAsExternal( out, q.x );
      Flo.floatAsExternal( out, q.y );
      Flo.floatAsExternal( out, q.z );
      }
   
   public void readExternal( InputStream in ) throws IOException
      {
      mQuat = Quat.externalAsQuaternion( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      Quat.quaternionAsExternal( out, mQuat );
      }
   
   public String prettyPrint()
      {
      return "(Quat " + mQuat + ")";
      }

   private Quaternion mQuat;
   }
