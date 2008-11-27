/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;


/**
 * @author dreish
 *
 */
public class ReplyInv extends Cookie
   {
   /**
    *
    */
   public ReplyInv()
      {
      super();
      }

   public ReplyInv( DvtpExternalizable key )
      {
      super( key );
      }

   public ReplyInv( DvtpExternalizable key, DvtpExternalizable value )
      {
      super( key, value );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 135;  }
   }
