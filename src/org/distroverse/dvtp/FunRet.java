package org.distroverse.dvtp;

/**
 * A list of arbitrary DvtpExternalizable objects.
 * @author dreish
 */
public class FunRet extends DList
   {
   public FunRet()
      {
      super();
      }
   
   public FunRet( DvtpExternalizable[] f )
      {
      super( f );
      }
   
   @Override
   public int getClassNumber()
      {  return 130;  }
   }
