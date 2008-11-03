/**
 *
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.distroverse.core.Util;

/**
 * @author dreish
 *
 */
public final class Dict implements DvtpExternalizable
   {

   /**
    *
    */
   public Dict()
      {
      mDict = new LinkedHashMap< DvtpExternalizable,
                                 DvtpExternalizable >();
      }

   /**
    * It is probably better to build up a dict using new Dict() and
    * Dict.put() rather than building up a LinkedHashMap and using this
    * constructor, in case the internal representation is changed and
    * this constructor imposes a translation performance penalty.
    * @param d
    */
   public Dict( LinkedHashMap< DvtpExternalizable,
                               DvtpExternalizable > d )
      {
      mDict = d;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 28;  }

   synchronized public DvtpExternalizable
   put( DvtpExternalizable key, DvtpExternalizable value )
      {  return mDict.put( key, value );  }

   synchronized public DvtpExternalizable get( DvtpExternalizable key )
      {
      return mDict.get( key );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      StringBuilder ret = new StringBuilder();
      ret.append( "(Dict" );
      for ( Map.Entry< DvtpExternalizable, DvtpExternalizable > pair
            : mDict.entrySet() )
         {
         ret.append( " (" );
         ret.append( Util.prettyPrintList( pair.getKey(),
                                           pair.getValue() ) );
         ret.append( ")" );
         }
      ret.append( ")" );
      return ret.toString();
      }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof Dict
           &&  ((Dict) o).mDict.size() == mDict.size() )
         {
         Iterator< Entry< DvtpExternalizable,
                          DvtpExternalizable >> other_iter
            = ((Dict) o).mDict.entrySet().iterator();
         for ( Map.Entry< DvtpExternalizable, DvtpExternalizable > pair
               : mDict.entrySet() )
            if ( ! pair.equals( other_iter.next() ) )
               return false;
         return true;
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      int ret = 0;
      for ( Map.Entry< DvtpExternalizable, DvtpExternalizable > pair
            : mDict.entrySet() )
         {
         ret ^= pair.hashCode();
         ret *= 852403729;
         }

      return ret;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   synchronized public void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      int num_pairs = Util.safeInt( ULong.externalAsLong( in ) );
      for ( int i = 0; i < num_pairs; ++i )
         mDict.put( DvtpObject.parseObject( in ),
                    DvtpObject.parseObject( in ) );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   synchronized public void writeExternal( OutputStream out )
   throws IOException
      {
      ULong.longAsExternal( out, mDict.size() );
      for ( Map.Entry< DvtpExternalizable, DvtpExternalizable > pair
            : mDict.entrySet() )
         {
         DvtpObject.writeInnerObject( out, pair.getKey() );
         DvtpObject.writeInnerObject( out, pair.getValue() );
         }
      }

   private LinkedHashMap< DvtpExternalizable,
                          DvtpExternalizable > mDict;
   }
