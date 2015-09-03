package com.camcars;

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

import java.util.*;

public class PSAssert {
    
    private static PSAssert _asserter = null;

    public static PSAssert asserter() {
        if (_asserter == null) {
            _asserter = new PSAssert();
        }
        return _asserter;
    }

    public void  assertTrue( boolean condition ) {
        assertTrue( null, condition );
    }

    public void assertFalse( boolean condition ) {
        assertFalse( null, condition );
    }

    public void assertTrue( String msg, boolean condition ) {
        if (condition != true) {
            throw new AssertionFailure( msg );
        }
    }

    public void assertFalse( String msg, boolean condition ) {
        if (condition != false) {
            throw new AssertionFailure( msg );
        }
    }
    
    public void assertEquals( String msg, Object object, Object model ) {
        if ( object == null && model == null )
            assertTrue( msg, true );
        else if ( object == null || model == null )
            assertTrue( msg, false );
        else
            assertTrue( msg, object.equals( model ) );
    }
    
    public void assertNotEquals( String msg, Object object, Object model ) {
        if ( object == null && model == null )
            assertTrue( msg, false );
        else if ( object == null || model == null )
            assertTrue( msg, true );
        else
            assertFalse( msg, object.equals( model ) );
    }

    /////////////////   I N N E R   C L A S S E S   /////////////////

     public static class AssertionFailure extends Error {

        public AssertionFailure( String message ) {
            super( message );
        }
    }
     
}


















