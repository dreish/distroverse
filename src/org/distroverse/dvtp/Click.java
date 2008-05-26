package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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

   public void readExternal( ObjectInput in ) throws IOException
      {
      mDirection.readExternal( in );
      mForce.readExternal( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      mDirection.writeExternal( out );
      mForce.writeExternal( out );
      }

   private Vec mDirection;
   private Flo mForce;
   }
