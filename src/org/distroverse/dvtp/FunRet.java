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

//immutable

/**
 * A list of arbitrary DvtpExternalizable objects.
 * @author dreish
 */
public class FunRet extends DList
   {
   public FunRet( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private FunRet()
      {
      super( (DvtpExternalizable) null );
      }
   
   public FunRet( DvtpExternalizable... f )
      {
      super( f );
      }
   
   @Override
   public int getClassNumber()
      {  return 130;  }

   @Override
   public String prettyPrint()
      {
      return "(org.distroverse.dvtp.FunRet. "
             + prettyPrintContents() + ")";
      }
   }
