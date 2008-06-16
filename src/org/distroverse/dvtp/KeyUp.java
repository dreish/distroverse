package org.distroverse.dvtp;

public class KeyUp extends Keystroke
   {
   public KeyUp()
      {  super();  }

   public KeyUp( int kn )
      {  super( kn );  }

   @Override
   public int getClassNumber()
      {  return 21;  }

   @Override
   public String prettyPrint()
      {
      return "(KeyUp " + getKey() + ")";
      }
   }
