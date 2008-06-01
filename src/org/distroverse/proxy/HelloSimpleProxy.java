package org.distroverse.proxy;

import org.distroverse.distroplane.lib.PrimFactory;
import org.distroverse.distroplane.lib.PrimFactory.PrimShape;
import org.distroverse.dvtp.ClientSendable;

/**
 * A simple demo proxy that does not connect to any server.
 * @author dreish
 */
public class HelloSimpleProxy extends ProxyBase
   {
   public void offer( ClientSendable o )
      {
      // Client input is ignored.
      }

   public void run()
      {
      PrimFactory pf = new PrimFactory();
      pf.setDims( 10.0, 1.0, 1.0 );
      addObject( pf.setPrimShape( PrimShape.SPHERE  ).generate(), 1L,
                 -30.0f, 10.0f, 40.0f );
      addObject( pf.setPrimShape( PrimShape.PYRAMID ).generate(), 2L,
                 +00.0f, 10.0f, 40.0f );
      addObject( pf.setPrimShape( PrimShape.CUBOID  ).generate(), 3L,
                 +30.0f, 10.0f, 40.0f );
      
      }
   }
