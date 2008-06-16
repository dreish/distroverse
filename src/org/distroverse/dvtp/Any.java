/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.distroverse.core.Util;

/**
 * This class is actually NOT part of DVTP, the protocol -- just dvtp,
 * the package.  None of the ClientSendable or ProxySendable classes use
 * this class.  The DvtpFlexiParser and DvtpFlexiStreamer will not send
 * or receive objects of this class, but the ExtraFlexiParser and
 * ExtraFlexiStreamer will.  It packs any Serializable object with
 * deflate compression.
 * 
 * TODO Any is completely unimplemented.
 * 
 * @author dreish
 */
public class Any implements DvtpExternalizable
   {

   /**
    * 
    */
   public Any()
      {
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {
      return DvtpObject.getSerializedClassNumber();
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   public String prettyPrint()
      {
      return "(Any " + Util.prettyPrintList( mContents ) + ")";
      }

   private Serializable mContents;
   }
