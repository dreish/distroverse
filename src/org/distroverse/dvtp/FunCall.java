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
 * A list of arbitrary DvtpExternalizable objects.  Typically the first
 * would be a unique identifier, to be the first member of the return
 * value as well, and the second member would typically be the name of a
 * function, or a serial number of a function, and the rest would be
 * arguments.
 *
 * @author dreish
 */
public class FunCall extends DList
   {
   public FunCall( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private FunCall()
      {
      super( (DvtpExternalizable) null );
      }

   public FunCall( DvtpExternalizable... f )
      {
      super( f );
      }

   @Override
   public int getClassNumber()
      {  return 129;  }

   @Override
   public String prettyPrint()
      {
      return "(FunCall " + prettyPrintContents() + ")";
      }
   }
