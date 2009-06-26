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
import java.util.LinkedHashMap;
import java.util.Map;

import org.distroverse.core.Util;

//immutable

/**
 * @author dreish
 *
 */
public final class Dict implements DvtpExternalizable
   {

   /**
    *
    */
   public Dict( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      int num_pairs = Util.safeInt( ULong.externalAsLong( in ) );
      mDict = new LinkedHashMap< DvtpExternalizable,
                                 DvtpExternalizable >();
      for ( int i = 0; i < num_pairs; ++i )
         mDict.put( DvtpObject.parseObject( in ),
                    DvtpObject.parseObject( in ) );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Dict()
      {
      mDict = null;
      }

   public Dict( Map< DvtpExternalizable, DvtpExternalizable > d )
      {
      mDict = new LinkedHashMap< DvtpExternalizable,
                                 DvtpExternalizable >();
      mDict.putAll( d );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 28;  }

   public DvtpExternalizable get( DvtpExternalizable key )
      {
      return mDict.get( key );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      StringBuilder ret = new StringBuilder();
      ret.append( "(org.distroverse.dvtp.Dict." );
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
         Dict d = (Dict) o;
         for ( Map.Entry< DvtpExternalizable, DvtpExternalizable > pair
               : mDict.entrySet() )
            if ( ! d.mDict.containsKey( pair.getKey() )
                 ||  ! d.mDict.get( pair.getKey() )
                              .equals( pair.getValue() ) )
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
         ret += pair.getKey().hashCode() * 852403729
                ^ pair.getValue().hashCode();
         }

      return ret;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out )
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

   private final Map< DvtpExternalizable, DvtpExternalizable > mDict;
   }
