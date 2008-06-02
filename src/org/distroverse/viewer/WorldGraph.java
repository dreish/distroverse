package org.distroverse.viewer;

import java.util.HashMap;

import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.Shape;

import com.jme.curve.CurveController;
import com.jme.scene.Node;


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
      if ( id < 0  ||  pid < 0 )
         throw new IllegalArgumentException( "ID must be nonnegative" );
      Node object_node = new Node( "dvo#" + id );
      Node parent_node = getNode( pid );
      if ( parent_node == null )
         // FIXME Throw exception here instead?
         parent_node = mWorldHead;
      mIdMap.put( id, new WorldGraphObject( object_node, pid ) );
      object_node.attachChild( s.asTriMesh() );
      setMoveSeq( init_move_seq, object_node );
//      object_node.setLocalTranslation( init_pos );
//      object_node.setLocalRotation( rotation );
      object_node.setLocalScale( 1 );
      System.out.println( object_node.getLocalScale() );
      parent_node.attachChild( object_node );
      }

   private void setMoveSeq( MoveSeq ms, Node object_node )
      {
      DvCurve curve = new DvCurve( object_node.getName() + "-mover",
                                   ms );
      CurveController cc = new CurveController( curve, object_node );
      object_node.addController( cc );
      // FIXME set repeat type
      }
   
   public void setMoveSeq( MoveSeq ms, Long id )
      {
      // TODO check for bad id
      setMoveSeq( ms, getNode( id ) );
      }

   public void deleteShape( Long id )
      {
      // TODO check for bad id
      Node object_node = getNode( id );
      object_node.removeFromParent();
      mIdMap.remove( id );
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
