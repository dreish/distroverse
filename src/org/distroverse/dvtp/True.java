package org.distroverse.dvtp;

/**
 * Truth.
 * @author dreish
 */
public class True extends Bool
   {
   public True()
      {  /* Do nothing. */  }

   public int getClassNumber()
      {  return 6;  }
   @Override
   public boolean asBoolean()
      {  return true;  }
   }
