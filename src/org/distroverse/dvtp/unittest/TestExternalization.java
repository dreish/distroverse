package org.distroverse.dvtp.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.Blob;
import org.distroverse.dvtp.Click;
import org.distroverse.dvtp.Click2;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.ConPerm;
import org.distroverse.dvtp.DList;
import org.distroverse.dvtp.DeleteObject;
import org.distroverse.dvtp.DisplayUrl;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpObject;
import org.distroverse.dvtp.Err;
import org.distroverse.dvtp.False;
import org.distroverse.dvtp.Flo;
import org.distroverse.dvtp.FunCall;
import org.distroverse.dvtp.FunRet;
import org.distroverse.dvtp.KeyDown;
import org.distroverse.dvtp.KeyUp;
import org.distroverse.dvtp.Keystroke;
import org.distroverse.dvtp.MoreDetail;
import org.distroverse.dvtp.Move;
import org.distroverse.dvtp.MoveObject;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.Pair;
import org.distroverse.dvtp.PointArray;
import org.distroverse.dvtp.ProxySpec;
import org.distroverse.dvtp.Quat;
import org.distroverse.dvtp.RedirectUrl;
import org.distroverse.dvtp.SetUrl;
import org.distroverse.dvtp.Shape;
import org.distroverse.dvtp.Str;
import org.distroverse.dvtp.True;
import org.distroverse.dvtp.Vec;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


public class TestExternalization
   {
   /**
    * Tests both correctness of externalization and correct
    * specialization of equals().
    * @param args
    */
   @SuppressWarnings("unchecked")
   public static void main( String[] args )
      {
      try
         {
         testExternalization();
         System.out.println( "All tests passed" );
         }
      catch ( Exception e )
         {
         System.out.println( e );
         e.printStackTrace();
         }
      }

   private static void testExternalization()
   throws IOException, ClassNotFoundException
      {
      for ( int i = 0; i < DvtpObject.mClassList.length; ++i )
         testClass( i );
      for ( int i = 0; i < DvtpObject.mExtendedClassList.length; ++i )
         testClass( i + 128 );
      }

   private static void testClass( int i )
   throws IOException, ClassNotFoundException
      {
      switch ( i )
         {
         case 0:   testCompactUlong();  break;
         case 1:   testPair();          break;
         case 2:   testStr();           break;
         case 3:   testBigInt();        break;
         case 4:   testPointArray();    break;
         case 5:   testFalse();         break;
         case 6:   testTrue();          break;
         case 7:   testDisplayUrl();    break;
         case 8:   testRedirectUrl();   break;
         case 9:   testSetUrl();        break;
         case 10:  testShape();         break;
         case 11:  testVec();           break;
         case 12:  testAddObject();     break;
         case 13:  testMove();          break;
         case 14:  testMoveObject();    break;
         case 15:  testFlo();           break;
         case 16:  testQuat();          break;
         case 17:  testDeleteObject();  break;
         case 18:  testMoveSeq();       break;
         case 19:  testKeystroke();     break;
         case 20:  testKeyDown();       break;
         case 21:  testKeyUp();         break;
         case 22:  testClick();         break;
         case 23:  testClick2();        break;
         case 24:  testMoreDetail();    break;
         case 25:  testBlob();          break;

         case 128: testDList();         break;
         case 129: testFunCall();       break;
         case 130: testFunRet();        break;
         case 131: testErr();           break;
         case 132: testConPerm();       break;
         case 133: testProxySpec();     break;
         }
      }

   private static void testCompactUlong()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { CompactUlong.MAX_VALUE, 0, 1, 127, 128, 129, 255, 256, 257,
                        16383, 16384, 16385, 2097152,
                        CompactUlong.MAX_VALUE };
      for ( long i : to_try )
         tryBeamObject( new CompactUlong( i ) );
      }

   private static void testPair()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Pair( new CompactUlong( 123 ),
                               new Str( "foo" ) ) );
      }

   private static void testStr()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Str( "foo" ) );
      tryBeamObject( new Str( "" ) );
      tryBeamObject( new Str( "\n\n\n\r\n\n" ) );
      tryBeamObject( new Str( "\0" ) );
      tryBeamObject( new Str( "Have you lost your dog?" ) );
      }

   private static void testBigInt()
//   throws IOException, ClassNotFoundException
      {
      // TODO Do I really care about this class?
      }

   private static void testPointArray()
   throws IOException, ClassNotFoundException
      {
      Vector3f[] p =
         {
         new Vector3f( 1, 2, 3 ),
         new Vector3f( 2f, 2.5f, 3f ),
         new Vector3f( 8f, -2.5f, 7f ),
         new Vector3f( -1, -1, -1 )
         };
      tryBeamObject( new PointArray( p ) );
      }

   private static void testFalse()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new False() );
      }

   private static void testTrue()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new True() );
      }

   private static void testDisplayUrl()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new DisplayUrl( "" ) );
      tryBeamObject( new DisplayUrl( "dvtp://example.com/foo" ) );
      tryBeamObject( new DisplayUrl( "a\nb\nc" ) );
      }

   private static void testRedirectUrl()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new RedirectUrl( "" ) );
      tryBeamObject( new RedirectUrl( "dvtp://example.com/foo" ) );
      tryBeamObject( new RedirectUrl( "a\nb\nc" ) );
      }

   private static void testSetUrl()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new SetUrl( "" ) );
      tryBeamObject( new SetUrl( "dvtp://example.com/foo" ) );
      tryBeamObject( new SetUrl( "a\nb\nc" ) );
      }

   private static void testShape()
   throws IOException, ClassNotFoundException
      {
      // XXX Write shapeExamples()
      Vector3f[] p =
         {
         new Vector3f( 1, 2, 3 ),
         new Vector3f( 2f, 2.5f, 3f ),
         new Vector3f( 8f, -2.5f, 7f ),
         new Vector3f( -1, -1, -1 )
         };
      tryBeamObject( new Shape( Arrays.asList( p ),
                                new int[] { 2, 2 } ) );
      }

   private static void testVec()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Vec( new Vector3f( 0, 0, 0 ) ) );
      tryBeamObject( new Vec( new Vector3f( 0, 1, 2 ) ) );
      tryBeamObject( new Vec( new Vector3f( 0, 0.5f, 1 ) ) );
      tryBeamObject( new Vec( new Vector3f( 1.5f, 2.5f, 3.5f ) ) );
      tryBeamObject( new Vec( new Vector3f( -1.5f, -2.5f, -3.5f ) ) );
      }

   private static void testAddObject()
   throws IOException, ClassNotFoundException
      {
      MoveSeq[] msex = moveSeqExamples();
      tryBeamObject( new AddObject( new CompactUlong( 10 ),
                                    new CompactUlong( 100 ),
                                    msex[ 0 ] ) );
      }

   private static MoveSeq[] moveSeqExamples()
      {
      Move[] mex = moveExamples();
      return new MoveSeq[]
         {
         new MoveSeq( new Move[] { mex[ 0 ] },
                      MoveSeq.RepeatType.ONCE ),
         new MoveSeq( new Move[] { mex[ 1 ], mex[ 0 ], mex[ 2 ] },
                      MoveSeq.RepeatType.BOUNCE ),
         new MoveSeq( new Move[] { mex[ 0 ], mex[ 1 ], mex[ 2 ] },
                      MoveSeq.RepeatType.LOOP ),
         };
      }

   private static Move[] moveExamples()
      {
      return new Move[]
         {
         new Move( new Vec( new Vector3f( 0.5f, 0.5f, 0.5f ) ),
                   new Quat( new Quaternion( 0, 0, 0, 1 ) ),
                   new Flo( 2 ) ),
         new Move( new Vec( new Vector3f( 1.5f, 1.5f, 1.5f ) ),
                   new Quat( new Quaternion( 0, 1, 0, 0 ) ),
                   new Flo( 2 ) ),
         new Move( new Vec( new Vector3f( 2.5f, 2.5f, 2.5f ) ),
                   new Quat( new Quaternion( 0, 0, 1, 0 ) ),
                   new Flo( 2 ) ),
         new Move(),
         };
      }

   private static void testMove()
   throws IOException, ClassNotFoundException
      {
      Move[] mex = moveExamples();
      for ( Move m : mex )
         tryBeamObject( m );
      }

   private static void testMoveObject()
   throws IOException, ClassNotFoundException
      {
      MoveSeq[] msex = moveSeqExamples();
      tryBeamObject( new MoveObject( 0, msex[ 0 ] ) );
      tryBeamObject( new MoveObject( 1, msex[ 1 ] ) );
      tryBeamObject( new MoveObject( 1001, msex[ 2 ] ) );
      }

   private static void testFlo()
   throws IOException, ClassNotFoundException
      {
      float[] to_try = { 0, Float.MAX_VALUE, Float.MIN_VALUE,
                         -Float.MAX_VALUE, -Float.MIN_VALUE,
                         1, 1.1f, -1, -1.1f };
      for ( float f : to_try )
         tryBeamObject( new Flo( f ) );
      }

   private static void testQuat()
   throws IOException, ClassNotFoundException
      {
      float[][] to_try =
         { { 0, 0, 0, 1 },
           { 1, 0, 0, 0 },
           { 0, 1, 0, 0 },
           { 0.5f, 0.2f, 0.3f, 0.1f },
           { 0.5f, 0.25f, 0.25f, 0.125f },
         };
      for ( float[] fa : to_try )
         tryBeamObject( new Quat( new Quaternion( fa[0], fa[1],
                                                  fa[2], fa[3] ) ) );
      }

   private static void testDeleteObject()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { 0, 1, 127, 128, 129, 255, 256, 257,
                        16383, 16384, 16385, 2097152,
                        CompactUlong.MAX_VALUE };
      for ( long i : to_try )
         tryBeamObject( new DeleteObject( i ) );
      }

   private static void testMoveSeq()
   throws IOException, ClassNotFoundException
      {
      MoveSeq[] msex = moveSeqExamples();
      for ( MoveSeq ms : msex )
         tryBeamObject( ms );
      }

   private static void testKeystroke()
   throws IOException, ClassNotFoundException
      {
      int[] to_try = { 0, 1, 127, 128, 129, 255, 256, 257,
                       16383, 16384, 16385, 2097152 };
      for ( int i : to_try )
         tryBeamObject( new Keystroke( i ) );
      }

   private static void testKeyDown()
   throws IOException, ClassNotFoundException
      {
      int[] to_try = { 0, 1, 127, 128, 129, 255, 256, 257,
                       16383, 16384, 16385, 2097152 };
      for ( int i : to_try )
         tryBeamObject( new KeyDown( i ) );
      }

   private static void testKeyUp()
   throws IOException, ClassNotFoundException
      {
      int[] to_try = { 0, 1, 127, 128, 129, 255, 256, 257,
                       16383, 16384, 16385, 2097152 };
      for ( int i : to_try )
         tryBeamObject( new KeyUp( i ) );
      }

   private static void testClick()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Click( new Vec( new Vector3f( 1, 2, 3 ) ),
                                new Flo( 0.5f ) ) );
      }

   private static void testClick2()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Click2( new Vec( new Vector3f( 1, 2, 3 ) ),
                                 new Flo( 0.5f ) ) );
      }

   private static void testMoreDetail()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new MoreDetail( 0.9f ) );
      tryBeamObject( new MoreDetail( 3 ) );
      }

   private static void testBlob()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Blob( "foo".getBytes(), 3, "bar", 5, 8 ) );
      }

   private static void testDList()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new DList( exampleDlist() ) );
      }

   private static DvtpExternalizable[] exampleDlist()
      {
      DvtpExternalizable[] d_list =
         {
         new Str( "add" ),
         new Flo( 3 ),
         new Flo( 4 ),
         new Flo( 5 ),
         };
      return d_list;
      }

   private static void testFunCall()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new FunCall( exampleDlist() ) );
      }

   private static void testFunRet()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new FunRet( exampleDlist() ) );
      }

   private static void testErr()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Err( "fooness not found", 404 ) );
      }

   private static void testConPerm()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new ConPerm( true,  "0" ) );
      tryBeamObject( new ConPerm( false, "dvtp://www.example.com/" ) );
      tryBeamObject( new ConPerm( false, "" ) );
      }

   private static void testProxySpec()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new ProxySpec( "a", "b", "c" ) );
      tryBeamObject( new ProxySpec( "", "", "" ) );
      tryBeamObject( new ProxySpec( "a:--//", "b$$##\\\\", "&" ) );
      }

   /**
    * Turn 'in' into a byte stream, and turn that byte stream back into
    * a DvtpExternalizable object, and then assert that the resulting
    * object equals() the original one.  Assumes equals() tests for true
    * equality rather than identity.
    * @param in
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   private static void
   tryBeamObject( DvtpExternalizable in )
   throws IOException, ClassNotFoundException
      {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DvtpObject.writeInnerObject( baos, in );
      ByteArrayInputStream bais
         = new ByteArrayInputStream( baos.toByteArray() );
      DvtpExternalizable out = DvtpObject.parseObject( bais );
      if ( ! out.equals( in ) )
         throw new RuntimeException( in.prettyPrint()
                                     + " != " + out.prettyPrint() );
      }
   }
