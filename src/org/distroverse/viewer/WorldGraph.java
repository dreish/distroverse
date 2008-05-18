package org.distroverse.viewer;

import java.util.HashMap;

import org.distroverse.dvtp.MoveSeq;
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
      mIdMap  = new HashMap< Long, WorldGraphObject >();
      }
   
   public void addShape( Shape s, Long id, Long pid,
                         MoveSeq init_move_seq )
      {
      Node object_node = new Node( "dvo#" + id );
      Node parent_node = getNode( pid );
      if ( parent_node == null )
         parent_node = mWorldHead;
      mIdMap.put( id, new WorldGraphObject( object_node, pid ) );
      object_node.attachChild( s.asTriMesh() );
      addMoveSeq( init_move_seq, object_node );
//      object_node.setLocalTranslation( init_pos );
//      object_node.setLocalRotation( rotation );
      object_node.setLocalScale( 1 );
      System.out.println( object_node.getLocalScale() );
      parent_node.attachChild( object_node );
      }

   private void addMoveSeq( MoveSeq init_move_seq, Node object_node )
      {
      // TODO Auto-generated method stub
      
      }

   public void clear()
      {
      mWorldHead.detachAllChildren();
      }

   private final class WorldGraphObject
      {
      public WorldGraphObject( Node n, Long pid )
         {  
         mNode = n;
         mParentId = pid;
         }
      public Node  getNode()      {  return mNode;  }
      public Long  getParentId()  {  return mParentId;  }
      private Node mNode;
      private Long mParentId;
      }
   
   private Node getNode( Long m )
      {
      WorldGraphObject wgo = mIdMap.get( m );
      if ( wgo != null )
         return wgo.getNode();
      return null;
      }

   private Node mParent;
   private Node mWorldHead;
   private HashMap< Long, WorldGraphObject > mIdMap;
   }
