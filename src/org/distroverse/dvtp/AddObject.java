package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Adds an object, with initial movement.
 * @author dreish
 */
public class AddObject implements ProxySendable
   {
   public AddObject()
      {
      super();
      }
   
   public AddObject( Shape s, CompactUlong id, CompactUlong pid,
                     Movement m )
      {
      super();
      mHasShape = true;
      mShape = s;
      mId = id;
      mParentId = pid;
      mMovement = m;
      }

   public AddObject( CompactUlong id, CompactUlong pid, Movement m )
      {
      super();
      mHasShape = false;
      mShape = null;
      mId = id;
      mParentId = pid;
      mMovement = m;
      }

   public int getClassNumber()
      {  return 12;  }
   
   public Shape        getShape()     {  return mShape;     }
   public CompactUlong getId()        {  return mId;        }
   public CompactUlong getParentId()  {  return mParentId;  }
   public Movement     getMovement()  {  return mMovement;  }

   public void readExternal( ObjectInput in )
   throws IOException, ClassNotFoundException
      {
      mHasShape = Bool.externalAsBoolean( in );
      if ( mHasShape )
         (mShape = new Shape()).readExternal( in );
      else
         mShape = null;
      (mId = new CompactUlong()).readExternal( in );
      (mParentId = new CompactUlong()).readExternal( in );
      (mMovement = new Movement()).readExternal( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      Bool.booleanAsExternal( out, mHasShape );
      if ( mHasShape )
         mShape.writeExternal( out );
      mId.writeExternal( out );
      mParentId.writeExternal( out );
      mMovement.writeExternal( out );
      }

   private boolean      mHasShape;
   private Shape        mShape;
   private CompactUlong mId;
   private CompactUlong mParentId;
   private Movement     mMovement;
   }
