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

import org.distroverse.core.Util;

import com.jme.math.Vector3f;

public class Vec implements DvtpExternalizable
   {
   public Vec()
      {
      super();
      mVec = new Vector3f();
      }

   public Vec( Vector3f v )
      {
      super();
      mVec = v;
      }

   public int getClassNumber()
      {  return 11;  }

   public Vector3f asVector3f()
      {
      return mVec;
      }

   public void readExternal( InputStream in ) throws IOException
      {
      mVec = new Vector3f( Flo.externalAsFloat( in ),
                           Flo.externalAsFloat( in ),
                           Flo.externalAsFloat( in ) );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      Flo.floatAsExternal( out, mVec.x );
      Flo.floatAsExternal( out, mVec.y );
      Flo.floatAsExternal( out, mVec.z );
      }
   
   public String prettyPrint()
      {
      return "(Vec " 
             + Util.prettyPrintList( mVec ) + ")";
      }
   
   private Vector3f mVec;
   }
