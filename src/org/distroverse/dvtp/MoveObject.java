package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Takes an object ID and a MoveSeq, and moves it.
 * @author dreish
 */
public class MoveObject implements ProxySendable
   {
   public MoveObject()
      {
      super();
      }
   
   public MoveObject( long id, MoveSeq m )
      {
      mId = id;
      mMoveSeq = m;
      }
   
   public int getClassNumber()
      {  return 14;  }
   
   public long    getId()       {  return mId;       }
   public MoveSeq getMoveSeq()  {  return mMoveSeq;  }

   public void readExternal( ObjectInput in )
   throws IOException, ClassNotFoundException
      {
      mId = CompactUlong.externalAsLong( in );
      (mMoveSeq = new MoveSeq()).readExternal( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      mMoveSeq.writeExternal( out );
      }

   private long mId;
   private MoveSeq mMoveSeq;
   }
