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

/**
 * @author dreish
 *
 */
public interface DvtpExternalizable
   {
   public abstract int getClassNumber();
   public abstract String prettyPrint();
   public abstract void readExternal( InputStream in )
   throws IOException, ClassNotFoundException;
   public abstract void writeExternal( OutputStream out )
   throws IOException;
   }
