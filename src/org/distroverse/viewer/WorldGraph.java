package org.distroverse.viewer;

import java.util.HashMap;

import org.distroverse.dvtp.Shape;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;


public class WorldGraph
   {
   public WorldGraph( Node parent )
      {
      mParent = parent;
      }
   
   public void addShape( Shape s, String id, String parent_id,
                         Vector3f position, Quaternion rotation )
      {
      // XXX do this
      Node object_node = new Node();
      Node parent_node = getNode( parent_id );
      mIdMap.put( id, new WorldGraphObject( object_node, parent_id ) );
      object_node.attachChild( s.asTriMesh() );
      object_node.setLocalTranslation( position );
      object_node.setLocalRotation( rotation );
      parent_node.attachChild( object_node );
      }
   
   public void clear()
      {
      mParent.detachAllChildren();
      }

   private final class WorldGraphObject
      {
      public WorldGraphObject( Node n, String parent_id )
         {  
         mNode = n;
         mParentId = parent_id;
         }
      public Node   getNode()      {  return mNode;  }
      public String getParentId()  {  return mParentId;  }
      private Node   mNode;
      private String mParentId;
      }
   
   private Node getNode( String id )
      {
      return mIdMap.get( id ).getNode();
      }

   private Node mParent;
   private HashMap< String, WorldGraphObject > mIdMap;
   }
