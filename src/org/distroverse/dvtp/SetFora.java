package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Set Frame-of-Reference Acceleration, for interfaces with the ability
 * to convey this information to the user, such as simulators that can
 * move.
 * @author dreish
 */
public final class SetFORA implements ProxySendable
   {
   public SetFORA( InputStream in ) 
   throws IOException, ClassNotFoundException
      {
      super();
      mMS = new MoveSeq( in );
      }
   
   public SetFORA( MoveSeq ms )
      {
      super();
      mMS = ms;
      }
   
   @SuppressWarnings("unused")
   private SetFORA()
      {
      mMS = null;
      }

   public MoveSeq getMoveSeq()
      {  return mMS;  }

   public int getClassNumber()
      {  return 141;  }

   public String prettyPrint()
      {  return "(SetFora " + mMS.prettyPrint() + ")";  }
   
   @Override
   public boolean equals( Object o )
      {
      return (o instanceof SetFora)
             &&  ((SetFora) o).mMS.equals( mMS );
      }
   
   @Override
   public int hashCode()
      {
      return SetFora.class.hashCode() ^ mMS.hashCode();
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      mMS.writeExternal( out );
      }

   private final MoveSeq mMS;
   }
