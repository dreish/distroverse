/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.Externalizable;

/**
 * @author dreish
 *
 */
public interface DvtpExternalizable extends Externalizable
   {
   public abstract int getClassNumber();
   }
