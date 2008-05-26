package org.distroverse.dvtp;

/**
 * A mouseclick with the secondary mouse button.  Typically calls a
 * context-dependent menu, so "force" may not be relevant in most cases.
 * @author dreish
 */
public class Click2 extends Click
   {
   public Click2()
      {  super();  }
   
   public Click2( Vec dir, Flo force )
      {  super( dir, force );  }

   @Override
   public int getClassNumber()
      {  return 23;  }
   }
