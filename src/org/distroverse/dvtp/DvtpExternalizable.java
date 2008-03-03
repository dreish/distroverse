/**
 * 
 */
package org.distroverse.dvtp;

import java.io.Externalizable;

/**
 * @author dreish
 *
 */
public interface DvtpExternalizable extends Externalizable
   {
   public abstract BigInt getClassNumber();
   }
