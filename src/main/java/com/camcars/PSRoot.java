
package com.camcars;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author patrice
 */
//public abstract class PSRoot implements PSCoding.Snapshoting {

public abstract class PSRoot  {

    private static PSLogger _logger = null;
    public static final String SNAPSHOT_CLASS_KEY = "className";

    protected static PSInstancePool pool() { 
        return PSInstancePool.defaultPool(); 
    }
    
    protected void runGarbageCollector() {
        long freeMem1 = Runtime.getRuntime().freeMemory();
        System.gc();
        long freeMem2 = Runtime.getRuntime().freeMemory();
        logger().trace( this, "Garbage collected: " +
                (freeMem2-freeMem1)+" bytes" );

    }

    public PSDict snapshot() {
        PSDict aDict = new PSDict(10);

        aDict.put( SNAPSHOT_CLASS_KEY, getClass().getName() );

        return aDict;
    }

  // This method should normally return a PSRoot but PSFault.Array has a version if this
  // method that returns a PSArray which is not a subclass of PSRoot. Both return types
  // aren't compatibles.
    public static Object objectFromSnapshot( PSDict aDict ) {
        Class aClass;
        PSRoot aContent = null;
        String className = (String )aDict.get( SNAPSHOT_CLASS_KEY );

        if ( className == null ) {
                logger().error( "PSObject: No key for class name in snapshot" );
                return null;
        }
        try {
            aClass = Class.forName( className );
            aContent = (PSRoot )aClass.newInstance();
            aContent.takePropertyDict( aDict );
        } catch (Exception e) {
            logger().error( "PSObject: Invalid snapshot", e );
            aContent = null;
        }

        return aContent;
    }

    public void takePropertyDict( PSDict dict ) { /* Live to be overridden */ }

    ///   L O G G I N G   //////////////////////////////////////////////////////
    public static void log( Object sender, String msg ) {
        logger().log( sender, msg );
    }

    public static void warn( Object sender, String msg ) {
        logger().warn( sender, msg );
    }

    public static void shout( Object sender, String msg ) {
        logger().shout( sender, msg );
    }

    public static void debug( Object sender, String msg ) {
        logger().debug( sender, msg );
    }

    public static void trace( Object sender, String msg ) {
        logger().trace( sender, msg );
    }

    public static void verbose( Object sender, String msg ) {
        logger().verbose( sender, msg );
    }

    public static void error( Object sender, String msg, Throwable e ) {
        logger().error( sender, msg, e );
    }

    public static void error( Object sender, String msg ) {
        logger().error( sender, msg );
    }

    public static void error( String msg, Throwable e ) {
        logger().error( msg, e );
    }

    public static void error( String msg ) {
        logger().error( msg );
    }

    protected static PSLogger logger() {
        if (_logger == null) {
            _logger = new PSLogger();
            _logger.setDebug(false);
            _logger.setTrace(false);
            _logger.setVerbose(false);
        }

        return _logger;
    }

    protected static void setLogger(PSLogger logger) {
        _logger = logger;
    }

    ///    A S S E R T I O N S   ///////////////////////////////////////////////
    public static PSAssert asserter() { return PSAssert.asserter(); }

    public void  assertTrue( boolean condition ) {
        asserter().assertTrue( null, condition );
    }

    public void assertFalse( boolean condition ) {
        asserter().assertFalse( null, condition );
    }

    public void assertTrue( String msg, boolean condition ) {
        asserter().assertTrue( msg, condition );
    }

    public void assertFalse( String msg, boolean condition ) {
        asserter().assertFalse( msg, condition );
    }

    public void assertEquals( String msg, Object object, Object model ) {
        asserter().assertEquals( msg, object, model );
    }

    public void assertNotEquals( String msg, Object object, Object model ) {
        asserter().assertNotEquals( msg, object, model );
    }

    public void assertTr( String msg, boolean testVal ) {
        assertTrue( msg + ": " + testVal + " != " + true, testVal );
        debug( this, msg + "...OK" );
    }

    public void assertFa( String msg, boolean testVal ) {
        assertTr( msg, ! testVal );
    }

    public void assertEq( String msg, int rightVal, int testVal ) {
        assertTrue( msg + ": " + rightVal + " != " + testVal,
                rightVal == testVal );
        debug( this, msg + ": [" + rightVal + "] = [" + testVal + "]" );
    }

    public void assertEq( String msg, Object rightObject, Object testObject ) {
        if ( rightObject instanceof Map || testObject instanceof Map ) {
            assertEq( msg, (Map )rightObject, (Map)testObject );
            return;
        }

        assertTrue( msg + ": [" + rightObject + "] != [" + testObject + "]",
                rightObject.equals( testObject ) );
        debug( this, msg + ": [" + rightObject + "] = [" + testObject + "]" );
    }

    public void assertEq( String msg, Collection rightVal, Collection testVal ) {
        Iterator rightIter = rightVal.iterator();
        Iterator testIter = testVal.iterator();

        System.out.print( getClass().getName() + " " + msg );
        assertEq( msg + " Testing Collection size: ", rightVal.size(), testVal.size() );
        while ( rightIter.hasNext() ) {
            Object rightObject = rightIter.next();
            Object testObject  = testIter.next();
            assertEq( msg, rightObject, testObject );
        }
        System.out.print( "...OK\n" );

    }

    public void assertEq( String msg, Map rightVal, Map testVal ) {
        Iterator rightIter = rightVal.keySet().iterator();
        Iterator testIter = testVal.keySet().iterator();

        System.out.print( getClass().getName() + " " + msg );
        assertEq( msg + " Test Map size: ", rightVal.size(), testVal.size() );
        while ( rightIter.hasNext() ) {
            Object rightKey = rightIter.next();
            Object rightObject = rightVal.get( rightKey );
            Object testObject  = rightVal.get( rightKey  );
            assertEq( msg, rightObject, testObject );
        }
        System.out.print( "...OK\n" );

    }

    ///   N E S T E D   C L A S S E S    /////////////////////////////////////////////////
    public interface Appending {

        public void appendTo( PSBuffer buffer );

    }
}
