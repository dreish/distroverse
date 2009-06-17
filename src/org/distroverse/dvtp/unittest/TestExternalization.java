/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;

import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.AskInv;
import org.distroverse.dvtp.Blob;
import org.distroverse.dvtp.CTrans;
import org.distroverse.dvtp.ClearShape;
import org.distroverse.dvtp.Click;
import org.distroverse.dvtp.Click2;
import org.distroverse.dvtp.Cookie;
import org.distroverse.dvtp.DList;
import org.distroverse.dvtp.DLong;
import org.distroverse.dvtp.DNode;
import org.distroverse.dvtp.DNodeRef;
import org.distroverse.dvtp.DeleteObject;
import org.distroverse.dvtp.Dict;
import org.distroverse.dvtp.DisplayUrl;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpObject;
import org.distroverse.dvtp.Err;
import org.distroverse.dvtp.False;
import org.distroverse.dvtp.Flo;
import org.distroverse.dvtp.Frac;
import org.distroverse.dvtp.FunCall;
import org.distroverse.dvtp.FunRet;
import org.distroverse.dvtp.GetCookie;
import org.distroverse.dvtp.KeyDown;
import org.distroverse.dvtp.KeyUp;
import org.distroverse.dvtp.Keystroke;
import org.distroverse.dvtp.MoreDetail;
import org.distroverse.dvtp.Move;
import org.distroverse.dvtp.MoveObject;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.Pair;
import org.distroverse.dvtp.PointArray;
import org.distroverse.dvtp.EnvoySpec;
import org.distroverse.dvtp.Quat;
import org.distroverse.dvtp.Real;
import org.distroverse.dvtp.RedirectUrl;
import org.distroverse.dvtp.ReparentObject;
import org.distroverse.dvtp.ReplyInv;
import org.distroverse.dvtp.SetFora;
import org.distroverse.dvtp.SetShape;
import org.distroverse.dvtp.SetUrl;
import org.distroverse.dvtp.SetVisible;
import org.distroverse.dvtp.Shape;
import org.distroverse.dvtp.Str;
import org.distroverse.dvtp.True;
import org.distroverse.dvtp.ULong;
import org.distroverse.dvtp.Vec;
import org.distroverse.dvtp.Warp;
import org.distroverse.dvtp.WarpObject;
import org.distroverse.dvtp.WarpSeq;

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
      for ( int i = 0;
            i < DvtpObject.mClassList.length - 1;
            ++i )
         testClass( i );
      for ( int i = 0;
            i < DvtpObject.mExtendedClassList.length - 1;
            ++i )
         testClass( i + 128 );
      }

   private static void testClass( int i )
   throws IOException, ClassNotFoundException
      {
      switch ( i )
         {
         case 0:   testCompactUlong();   break;
         case 1:   testPair();           break;
         case 2:   testStr();            break;
         case 3:   testBigInt();         break;
         case 4:   testPointArray();     break;
         case 5:   testFalse();          break;
         case 6:   testTrue();           break;
         case 7:   testDisplayUrl();     break;
         case 8:   testRedirectUrl();    break;
         case 9:   testSetUrl();         break;
         case 10:  testShape();          break;
         case 11:  testVec();            break;
         case 12:  testAddObject();      break;
         case 13:  testMove();           break;
         case 14:  testMoveObject();     break;
         case 15:  testFlo();            break;
         case 16:  testQuat();           break;
         case 17:  testDeleteObject();   break;
         case 18:  testMoveSeq();        break;
         case 19:  testKeystroke();      break;
         case 20:  testKeyDown();        break;
         case 21:  testKeyUp();          break;
         case 22:  testClick();          break;
         case 23:  testClick2();         break;
         case 24:  testMoreDetail();     break;
         case 25:  testBlob();           break;
         case 26:  testGetCookie();      break;
         case 27:  testCookie();         break;
         case 28:  testDict();           break;
         case 29:  testDNodeRef();       break;
         case 30:  testDNode();          break;
         case 31:  testDLong();          break;
         case 32:  testFrac();           break;
         case 33:  testReal();           break;
         case 34:  testWarp();           break;
         case 35:  testWarpSeq();        break;

         case 128: testDList();          break;
         case 129: testFunCall();        break;
         case 130: testFunRet();         break;
         case 131: testErr();            break;
         case 132: testSetVisible();     break;
         case 133: testEnvoySpec();      break;
         case 134: testAskInv();         break;
         case 135: testReplyInv();       break;
         case 136: testSetShape();       break;
         case 137: testWarpObject();     break;
         case 138: testReparentObject(); break;
         case 139: testClearShape();     break;
         case 140: testCTrans();         break;
         case 141: testSetFORA();        break;

         default:
            throw new ClassNotFoundException( "No test case for "
                                + DvtpObject.getClassByNumber( i )
                                + " (" + i + ")" );
         }
      }

   private static void testCompactUlong()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { ULong.MAX_VALUE, 0, 1, 127, 128, 129, 255, 256,
                        257, 16383, 16384, 16385, 2097152,
                        ULong.MAX_VALUE };
      for ( long i : to_try )
         tryBeamObject( new ULong( i ) );
      }

   private static void testPair()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Pair( new ULong( 123 ),
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
      Shape[] sex = shapeExamples();
      tryBeamObject( sex[ 0 ] );
      }

   private static Shape[] shapeExamples()
      {
      Vector3f[] p =
         {
         new Vector3f( 1, 2, 3 ),
         new Vector3f( 2f, 2.5f, 3f ),
         new Vector3f( 8f, -2.5f, 7f ),
         new Vector3f( -1, -1, -1 )
         };
      return new Shape[]
         {
         new Shape( Arrays.asList( p ),
                    new int[] { 2, 2 } ),
         };
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
      AddObject[] aoex = addObjectExamples();
      for ( AddObject ao : aoex )
         tryBeamObject( ao );
      }

   private static AddObject[] addObjectExamples()
      {
      MoveSeq[] msex = moveSeqExamples();
      Shape[]   sex = shapeExamples();
      return new AddObject[]
         {
         new AddObject( new ULong( 10 ),
                        new ULong( 100 ),
                        msex[ 0 ] ),
         new AddObject( true,
                        sex[ 0 ],
                        new ULong( 15 ),
                        new ULong( 171 ),
                        msex[ 1 ],
                        new WarpSeq() ),
         };
      }

   private static MoveSeq[] moveSeqExamples()
      {
      Move[] mex = moveExamples();
      return new MoveSeq[]
         {
         new MoveSeq( new Move[] { mex[ 0 ] },
                      MoveSeq.RepeatType.ONCE,
                      new Real( BigDecimal.ZERO ) ),
         new MoveSeq( new Move[] { mex[ 1 ], mex[ 0 ], mex[ 2 ] },
                      MoveSeq.RepeatType.BOUNCE,
                      new Real( new BigDecimal( 15.5 ) ) ),
         new MoveSeq( new Move[] { mex[ 0 ], mex[ 1 ], mex[ 2 ] },
                      MoveSeq.RepeatType.LOOP,
                      new Real( BigDecimal.valueOf( 1234, 3 ) ) ),
         };
      }

   private static Move[] moveExamples()
      {
      return new Move[]
         {
         new Move( new Vec( new Vector3f( 0.5f, 0.5f, 0.5f ) ),
                   new Quat( new Quaternion( 0, 0, 0, 1 ) ),
                   new Real( 2.0 ) ),
         new Move( new Vec( new Vector3f( 1.5f, 1.5f, 1.5f ) ),
                   new Quat( new Quaternion( 0, 1, 0, 0 ) ),
                   new Real( 2.0 ) ),
         new Move( new Vec( new Vector3f( 2.5f, 2.5f, 2.5f ) ),
                   new Quat( new Quaternion( 0, 0, 1, 0 ) ),
                   new Real( 2.0 ) ),
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
                        ULong.MAX_VALUE };
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

   private static void testGetCookie()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new GetCookie( new Str( "foo" ) ) );
      tryBeamObject( new GetCookie(
                        new EnvoySpec( "a:--//", "b$$##\\\\", "&" ) ) );
      Move[] mex = moveExamples();
      tryBeamObject( new GetCookie( mex[ 1 ] ));
      }

   private static void testCookie()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Cookie( new Str( "foo" ) ) );
      tryBeamObject( new Cookie( new Str( "foo" ), new Str( "bar" ) ) );
      tryBeamObject( new Cookie(
                        new EnvoySpec( "a:--//", "b$$##\\\\", "&" ) ) );
      tryBeamObject( new Cookie(
                          new EnvoySpec( "a:--//", "b$$##\\\\", "&" ),
                          new Flo( 3.5f ) ) );
      }

   private static void testDict()
   throws IOException, ClassNotFoundException
      {
      HashMap< DvtpExternalizable, DvtpExternalizable > m
         = new HashMap< DvtpExternalizable, DvtpExternalizable >();
      tryBeamObject( new Dict( m ) );
      m.put( new Str( "foo" ), new Str( "bar" ) );
      tryBeamObject( new Dict( m ) );
      m.put( new Flo( 1.23f ), new GetCookie( new Str( "bleh" ) ) );
      tryBeamObject( new Dict( m ) );
      }

   private static DNodeRef[] dNodeExamples()
      {
      return new DNodeRef[]
         {
         new DNodeRef( "0", 123L,
                       new Real( new BigDecimal( "123.45" ) ),
                       null ),
         new DNodeRef( "example.com", 456L,
                       new Real( new BigDecimal( "12.777" ) ), null ),
         new DNodeRef( "0", 125L,
                       new Real( new BigDecimal( "123.45" ) ), null ),
         };
      }

   private static void testDNodeRef()
   throws IOException, ClassNotFoundException
      {
      DNodeRef[] dnex = dNodeExamples();
      for ( DNodeRef dn : dnex )
         tryBeamObject( dn );
      }

   private static void testDNode()
   throws IOException, ClassNotFoundException
      {
      AddObject[] aoex = addObjectExamples();
      DNodeRef[]  dnex = dNodeExamples();
      tryBeamObject(
         new DNode( aoex[ 0 ], 2.0f, dnex[ 0 ], dnex[ 1 ],
                    new DNodeRef[] {}, 8 )
         );
      tryBeamObject(
         new DNode( aoex[ 0 ], 2.0f, dnex[ 0 ], dnex[ 1 ],
                    dnex, 6 )
         );
      tryBeamObject(
         new DNode( aoex[ 0 ], 3.0f, dnex[ 1 ], dnex[ 2 ],
                    "gen-children", new DvtpExternalizable[]
                                       { new ULong( 1 ) }, 5 )
         );
      tryBeamObject(
         new DNode( aoex[ 0 ], 3.0f, dnex[ 1 ], dnex[ 2 ],
                    "gen-children", new DvtpExternalizable[]
                                       { new ULong( 1 ),
                                         new Flo( 2.2f ) }, 3 )
         );
      }

   private static void testDLong()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { DLong.MAX_VALUE, 0, 1, 127, 128, 129, 255, 256,
                        257, 16383, 16384, 16385, 2097152,
                        -1, -127, -128, -129, -255, -256, -257, -16383,
                        -16384, -16385, -2097152, DLong.MIN_VALUE, };
      for ( long i : to_try )
         tryBeamObject( new DLong( i ) );
      }

   private static void testFrac()
   throws IOException, ClassNotFoundException
      {
      double[] to_try = { 0.1, 0.3, 0.5, 0.7, 0.9 };
      for ( double d : to_try )
         {
         tryBeamObject( Frac.getNew( d, 3  ) );
         tryBeamObject( Frac.getNew( d, 7  ) );
         tryBeamObject( Frac.getNew( d, 10 ) );
         tryBeamObject( Frac.getNew( d, 14 ) );
         tryBeamObject( Frac.getNew( d, 63 ) );
         }
      }

   private static void testReal()
   throws IOException, ClassNotFoundException
      {
      double[] to_try = { 0.001,  0.01,  0.1,  0.3,  0.5,  0.7,  0.9,
                         -0.001, -0.01, -0.1, -0.3, -0.5, -0.7, -0.9 };
      double[] to_add = { 0, 2, -2, 4, -4, 1000, -1000 };
      for ( double d : to_try )
         {
         for ( double da : to_add )
            {
            tryBeamObject( new Real( new BigDecimal( d + da ) ) );
            tryBeamObject( new Real( new BigDecimal(
                    d + da, MathContext.DECIMAL32 ) ) );
            tryBeamObject( new Real( new BigDecimal(
                    d + da, MathContext.DECIMAL64 ) ) );
            tryBeamObject( new Real( new BigDecimal(
                    d + da, MathContext.DECIMAL128 ) ) );
            }
         }
      }

   private static void testWarp()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new Warp( new PointArray[ 0 ] ) );
      // FIXME need to try some non-empty Warps
      }

   private static void testWarpSeq()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new WarpSeq( new Warp[ 0 ],
                                  WarpSeq.RepeatType.LOOP ) );
      Warp[] sequence = { new Warp( new PointArray[ 0 ] ) };
      tryBeamObject( new WarpSeq( sequence,
                                  WarpSeq.RepeatType.ONCE ) );
      // FIXME need to try some non-empty Warps
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

   private static void testEnvoySpec()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new EnvoySpec( "a", "b", "c" ) );
      tryBeamObject( new EnvoySpec( "", "", "" ) );
      tryBeamObject( new EnvoySpec( "a:--//", "b$$##\\\\", "&" ) );
      }

   private static void testAskInv()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new AskInv( "foo", new Str( "bar" ) ) );
      tryBeamObject( new AskInv( "ID", new Flo( 1.4f ) ) );
      }

   private static void testReplyInv()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new ReplyInv( new Str( "bar" ) ) );
      tryBeamObject( new ReplyInv( new Str( "bar" ),
                                   new Str( "barx" ) ) );
      }

   private static void testSetShape()
   throws IOException, ClassNotFoundException
      {
      Shape[] sex = shapeExamples();
      tryBeamObject( new SetShape( 1, sex[ 0 ],
                                   new WarpSeq( new Warp[ 0 ],
                                          WarpSeq.RepeatType.ONCE ) ) );
      }

   private static void testWarpObject()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new WarpObject( 1,
                             new WarpSeq( new Warp[ 0 ],
                                          WarpSeq.RepeatType.ONCE ) ) );
      }

   private static void testReparentObject()
   throws IOException, ClassNotFoundException
      {
      tryBeamObject( new ReparentObject( 3, 4 ) );
      tryBeamObject( new ReparentObject( 0, 0 ) );
      tryBeamObject( new ReparentObject( ULong.MAX_VALUE,
                                         ULong.MAX_VALUE ) );
      }

   private static void testClearShape()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { DLong.MAX_VALUE, 0, 1, 127, 128, 129, 255, 256,
                        257, 16383, 16384, 16385, 2097152, };
      for ( long i : to_try )
         tryBeamObject( new ClearShape( i ) );
      }

   private static void testSetVisible()
   throws IOException, ClassNotFoundException
      {
      long[] to_try = { DLong.MAX_VALUE, 0, 1, 127, 128, 129, 255, 256,
                        257, 16383, 16384, 16385, 2097152, };
      for ( long i : to_try )
         {
         tryBeamObject( new SetVisible( i, true ) );
         tryBeamObject( new SetVisible( i, false ) );
         }
      }

   private static void testCTrans()
   throws IOException, ClassNotFoundException
      {
      AddObject[] aoex = addObjectExamples();
      tryBeamObject( new CTrans( aoex[ 0 ] ) );
      tryBeamObject( new CTrans( aoex ) );
      tryBeamObject( new CTrans( aoex[ 0 ], aoex[ 1 ],
                                 new SetVisible( 125, true ) ) );
      }

   private static void testSetFORA()
   throws IOException, ClassNotFoundException
      {
      MoveSeq[] msex = moveSeqExamples();
      for ( MoveSeq ms : msex )
         tryBeamObject( new SetFora( ms ) );
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
      if ( out.hashCode() != in.hashCode() )
         throw new RuntimeException( "hashcode for " + in.prettyPrint()
                                     + " does not match hashcode for "
                                     + out.prettyPrint() );
      }
   }
