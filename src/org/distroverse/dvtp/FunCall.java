package org.distroverse.dvtp;

/**
 * A list of arbitrary DvtpExternalizable objects.  Typically the first
 * would be the name of a function, or a serial number of a function,
 * and the rest would be arguments.
 * @author dreish
 */
public class FunCall extends DList
   {
   public FunCall()
      {
      super();
      }
   
   public FunCall( DvtpExternalizable[] f )
      {
      super( f );
      }
   
   @Override
   public int getClassNumber()
      {  return 129;  }
   }
