package org.distroverse.dvtp;

import org.distroverse.core.Util;

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

   @Override
   public String prettyPrint()
      {
      return "(FunRet " 
             + Util.prettyPrintList( (Object[]) getContents() ) + ")";
      }
   }
