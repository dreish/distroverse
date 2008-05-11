package org.distroverse.dvtp;

/**
 * Falsity.
 * @author dreish
 */
public class False extends Bool
   {
   public False()
      {  /* Do nothing. */  }

   public int getClassNumber()
      {  return 5;  }
   @Override
   public boolean asBoolean()
      {  return false;  }
   }
