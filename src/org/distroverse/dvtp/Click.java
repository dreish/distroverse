package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

/**
 * A mouseclick with the primary (index finger) mouse button.  Typically
 * represents a touch, grab, or pull.
 * @author dreish
 */
public class Click implements ClientSendable
   {
   public Click()
      {
      super();
      }
   
   public Click( Vec dir, Flo force )
      {
      super();
      mDirection = dir;
      mForce     = force;
      }
   
   public int getClassNumber()
      {  return 22;  }
   public Vec getDirection()
      {  return mDirection;  }
   public Flo getForce()
      {  return mForce;  }
   public void setDirForce( Vec dir, Flo force )
      {
      mDirection = dir;
      mForce = force;
      }

   public void readExternal( InputStream in ) throws IOException
      {
      (mDirection = new Vec()).readExternal( in );
      (mForce = new Flo()).readExternal( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      mDirection.writeExternal( out );
      mForce.writeExternal( out );
      }
   
   public String prettyPrint()
      {
      return "(Click " 
             + Util.prettyPrintList( mDirection, mForce ) + ")";
      }

   private Vec mDirection;
   private Flo mForce;
   }
