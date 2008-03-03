package org.distroverse.core;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

public class NetInQueue< T >
   {
   public NetInQueue( ObjectRecognizer< T > os,
                      int max_length,
                      Selector s,
                      SocketChannel client )
      {
      mContents       = new LinkedList< T >();
      mMaxLength      = max_length;
      mObjectStreamer = os;
      os.setQueue( this );
      mWriterKey      = null;
      mSelector       = s;
      mClient         = client;
      }

   }
