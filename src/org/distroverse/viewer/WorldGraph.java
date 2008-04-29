package org.distroverse.viewer;

import java.util.HashMap;

import org.distroverse.dvtp.Shape;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;


public class WorldGraph
   {
   public WorldGraph( Node parent )
      {
      mParent = parent;
      mWorldHead = new Node( "WorldHead" );
      mParent.attachChild( mWorldHead );
      mIdMap  = new HashMap< String, WorldGraphObject >();
      }
   
   public void addShape( Shape s, String id, String parent_id,
                         Vector3f position, Quaternion rotation )
      {
      Node object_node = new Node( id );
      Node parent_node = getNode( parent_id );
      if ( parent_node == null )
         parent_node = mWorldHead;
      mIdMap.put( id, new WorldGraphObject( object_node, parent_id ) );
      object_node.attachChild( s.asTriMesh() );
      object_node.setLocalTranslation( position );
      object_node.setLocalRotation( rotation );
      object_node.setLocalScale( 10 );
      System.out.println( object_node.getLocalScale() );
      parent_node.attachChild( object_node );
      }

   public void clear()
      {
      mWorldHead.detachAllChildren();
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
      WorldGraphObject wgo = mIdMap.get( id );
      if ( wgo != null )
         return wgo.getNode();
      return null;
      }

   private Node mParent;
   private Node mWorldHead;
   private HashMap< String, WorldGraphObject > mIdMap;
   }
