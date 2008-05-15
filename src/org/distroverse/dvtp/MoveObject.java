package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Takes an object ID and a Movement, and moves it.
 * @author dreish
 */
public class MoveObject implements ProxySendable
   {
   public MoveObject()
      {
      super();
      }
   
   public MoveObject( long id, Movement m )
      {
      mId = id;
      mMovement = m;
      }
   
   public int getClassNumber()
      {  return 14;  }
   
   public long     getId()        {  return mId;        }
   public Movement getMovement()  {  return mMovement;  }

   public void readExternal( ObjectInput in )
   throws IOException, ClassNotFoundException
      {
      mId = CompactUlong.externalAsLong( in );
      (mMovement = new Movement()).readExternal( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      mMovement.writeExternal( out );
      }

   private long mId;
   private Movement mMovement;
   }
