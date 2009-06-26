/**
 * 
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.distroverse.core.Util;

//immutable

/**
 * Client transaction; all the messages within this transaction are
 * applied atomically, with no chance the user will see a frame drawn
 * with only some of the changes made.
 * 
 * @author dreish
 */
public class CTrans implements EnvoySendable
   {
   public CTrans( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      int length = Util.safeInt( ULong.externalAsLong( in ) );
      mContents = new EnvoySendable[ length ];
      for ( int i = 0; i < length; ++i )
         try
            {
            mContents[ i ]
               = (EnvoySendable) DvtpObject.parseObject( in );
            }
         catch ( ClassCastException e )
            {
            throw new ClassNotFoundException( e.getMessage() );
            }
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private CTrans()
      {
      mContents = null;
      }

   public CTrans( EnvoySendable... f )
      {
      super();
      mContents = f.clone();
      }

   public CTrans( EnvoySendable f )
      {
      super();
      mContents = new EnvoySendable[] { f };
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 140;  }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( this.getClass() )
              && Arrays.equals( mContents, ((CTrans) o).mContents ));
      }

   @Override
   public int hashCode()
      {
      return Arrays.hashCode( mContents )
             ^ this.getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mContents.length );
      for ( DvtpExternalizable o : mContents )
         DvtpObject.writeInnerObject( out, o );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(org.distroverse.dvtp.CTrans. "
             + Util.prettyPrintList( (Object[]) mContents ) + ")";
      }

   private final EnvoySendable[] mContents;
   }
