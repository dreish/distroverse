/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author dreish
 *
 */
public interface DvtpExternalizable
   {
   public abstract int getClassNumber();
   public abstract String prettyPrint();
   public abstract void writeExternal( OutputStream out )
   throws IOException;
   public boolean equals( Object o );
   public int hashCode();
   }
