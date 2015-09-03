
package com.camcars.test;

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

import com.camcars.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Test extends PSObject {
    
    private Chrono      _chrono = null;
    private String      _description = null;
    private TestResult  _result;
    private PSArray     _trueAssertions;
    private PSArray     _falseAssertions;
    private TestSuite   _testSuite = null;
    private long        _elapsed = 0;
    
    public Test() {
        _trueAssertions = new PSArray();
        _falseAssertions = new PSArray();
    }
    
    public Test( String description ) {
        this();
        _description = description;        
    }
    
    
    ///   T E S T I N G   //////////////////////////////////////////////////////
    public abstract void test() throws Exception;
    public void init() { }
    public void tearDown() { }
    
    /// P E R S I S T E N C E  /////////////////////////////////////////////////
    public void takePropertyDict( PSDict dict ) {}
    
    ///   A C C E S S O R S   //////////////////////////////////////////////////
    public TestSuite testSuite() { return _testSuite; }
    public void setTestSuite( TestSuite testSuite ) { _testSuite = testSuite; }
    
    protected static PSLogger logger() { return  TestSuite.logger(); }
    
    public String description() {
        if ( _description == null ) {
            _description = getClass().getName();
        }
        
        return _description;
    }
    
    public void setDescription( String desc ) { _description = desc; }
    
    public TestResult result() { return _result; }
    
    public Collection trueAssertions() { return _trueAssertions; }    
    public Collection falseAssertions() { return _falseAssertions; }
    
    protected long elapsed() { return _elapsed; }
    protected void setElapsed( long t ) { _elapsed = t; }
        
    public Chrono chrono() { return _chrono; }
    public void setChrono( String desc ) {
        _chrono = startChrono( desc );
    }
    
    
    ///   R E S U L T   H A N D L I N G   //////////////////////////////////////
    private void setResult( TestResult result ) {
        _result = result;
    }
    
//    public void setSuccess( long t ) {
//        setResult( new TestSuccess( t ) );
//    }
//
//    public void setError( long t, Throwable e ) {
//        setResult( new TestError( t, e ) );
//    }
//
//    public void setFailure( long t, Throwable e ) {
//        setResult( new TestFailure( t, e ) );
//    }
    
    public void setSuccess() {
        setResult( new TestSuccess( elapsed() ) );
    }
    
    public void setError( Throwable e ) {
        setResult( new TestError( elapsed(), e ) );
    }
    
    public void setFailure( Throwable e ) {
        setResult( new TestFailure( elapsed(), e ) );
    }
    
    public void fail() {
        fail(null);
    }
    
    public void fail( String msg ) {
        assertTrue( msg, false );
    }
    
    public void succeed() {
        succeed(null);
    }
    
    public void succeed( String msg ) {
        assertTrue( msg, true );
    }
    
    ///   A S S E R T I O N S   ////////////////////////////////////////////////
    private void trueAssertStore( String msg ) {
        if (msg != null)
            _trueAssertions.addElement(msg);
    }
    
    private void falseAssertStore( String msg ) {
        if (msg != null)
            _falseAssertions.addElement(msg);
    }
    
    public void assertTrue( String msg, boolean condition ) {
        super.assertTrue( msg, condition );
        trueAssertStore( msg );
    }
    
    public void assertFalse( String msg, boolean condition ) {
        super.assertFalse( msg, condition );
        falseAssertStore( msg );
    }
    
    public void assertEquals( String msg, Object object, Object model ) {
        super.assertEquals( msg, object, model );
        trueAssertStore( msg );
    }
    
    public void assertNotEquals( String msg, Object object, Object model ) {
        super.assertNotEquals( msg, object, model );
        falseAssertStore( msg );
    }

    ///   C H R O N O   ////////////////////////////////////////////////////////
    public Chrono startChrono( String desc ) {
        return startChrono( this, desc );
    }
    
    public Chrono startChrono( PSObject owner, String desc ) {
        return new Chrono( owner, desc );
    }
    
    public Chrono startChrono( String desc, String subject ) {
        return new Chrono( this, desc, subject );
    }
    
    public void chronoTest( String methodName, String desc ) {
        chronoTest( methodName, desc, null );
    }
    
    public void chronoTest( PSObject owner, String methodName, String desc ) {
        chronoTest( owner, methodName, desc, null );
    }
    
    public void chronoTest( String methodName, String desc, String testSubjectKey ) {
        chronoTest( this, methodName, desc, testSubjectKey );
    }
     
    public void chronoTest( PSObject owner, String methodName, String desc, String testSubjectKey ) {
        String targetKey = null, subjectKey = null;
        
        try {
            PSAction action = new PSAction( owner, targetKey, methodName, subjectKey );
            Chrono chrono = startChrono( desc, testSubjectKey );
            action.performAction();
            chrono.stop();
        } catch (Throwable ex) {
            String msg = "Error during chronoTest";
            logger().error( this, msg, ex );
            fail( msg );
        }
    }
    
    ///   U T I L I T I E S   //////////////////////////////////////////////////
    public String toString() {
        return getClass().getName() + "[" + "d=\"" + description() +
                "\" r=" + result().toString() + "]";
    }
    
    ///   I N N E R   C L A S S E S   //////////////////////////////////////////    
    public abstract class  TestResult {
        
        protected long _elapsedTime;
        protected Throwable _exception;
        
        public TestResult(long elapsedTime, Throwable exception) {
            _elapsedTime = elapsedTime;
            _exception = exception;
        }
        
        public long elapsedTime() { return _elapsedTime; }
        
        public Throwable exception() { return _exception; }
        
        public void setException( Throwable exception ) {
            _exception = exception;
        }
        
        public void setElapsedTime( long t ) {
            _elapsedTime = t;
        }
        
        public String toString() {
            return getClass().getName() + "[" +
                    "t=" + elapsedTime() + "ms; " +
                    "e=" + description( exception() ) + "]";
            
        }
        
        private String description( Throwable e ) {
            if (e == null) return  null;
            return e.getClass().getName() + "[" + e.getMessage() + "]";
        }
        
        public abstract boolean wasSuccessful();
        public abstract boolean wasFailure();
        public abstract boolean wasError();
        
    }
    
    class TestSuccess extends TestResult {
        
        public TestSuccess( long elapsedTime ) {
            super( elapsedTime, null );
        }
        
        public boolean wasSuccessful() { return true; }
        public boolean wasFailure()  { return false; }
        public boolean wasError()  { return false; }
        
    }
    
    class TestFailure extends TestResult {
        
        public TestFailure( long elapsedTime, Throwable exception ) {
            super( elapsedTime, exception );
        }
        
        public boolean wasSuccessful() { return false; }
        public boolean wasFailure()  { return true; }
        public boolean wasError()  { return false; }
    }
    
    class TestError extends TestResult {
        
        public TestError( long elapsedTime, Throwable exception ) {
            super( elapsedTime, exception );
        }
        
        public boolean wasSuccessful() { return false; }
        public boolean wasFailure()  { return false; }
        public boolean wasError()  { return true; }
        
    }
    
    public static class Chrono {
        
        private PSObject _owner;
        private PSObject _subject;
        private String _subjectKey;
        private String _runDesc;
        private long _startTime;
        private long _stopTime;
        
        public Chrono( PSObject owner, String runDesc) {
            this( owner, runDesc, null );
        }
        
        public Chrono( PSObject owner, String runDesc, String subjectKey ) {
            _owner = owner;
            setSubjectKey(subjectKey);
            _runDesc = runDesc;
            _startTime = System.currentTimeMillis();
            _shout( runDesc + "..." );
        }
        
        public PSObject owner() { return _owner; }
        
        public PSTarget target() {
            return new PSTarget( owner() );
        }
        
        public String subjectKey() {
            return _subjectKey;
        }
        
        public void setSubjectKey(String subjectKey) {
            this._subjectKey = subjectKey;
        }
        
        public PSObject subject() {
            if (_subject != null) return _subject;
            if (subjectKey() != null) {
                try {
                    return (PSObject )target().performMethod( subjectKey() );
                } catch (InvocationTargetException ex) {
                    logger().error( this, "Error while resolving subject. Using null.", ex );
                    return null;
                }
            } else
                return null;
        }
        
        public void setSubject( PSObject subject ) { _subject = subject; }
        
        public String runDesc() { return _runDesc; }
        
        private void _shout( String msg ) {
            logger().shout( owner(), msg );
        }
        
        /**
         * We use warn rather than log so that the mute can stop the msg.
         */
        private void _log( String msg ) {
            if ( owner() instanceof MRun ) {
                logger().alterMute(false);
                logger().debug( owner(), msg );
                logger().restoreMute();
            } else {
                logger().debug( owner(), msg );
            }
            
        }
        
        private void _stop() { _stopTime = System.currentTimeMillis(); }
        
        public void stop() {
            _stop();
            if ( subject() != null )
                _log( "Finished " + runDesc() + ": subject=["+ subject() + "] ti=" + elapsedTime() + "ms" );
            else
                _log( "Finished " + runDesc() + " ti=" + elapsedTime() + "ms" );
        }
        
        public void stop( PSObject subject ) {
            setSubject( subject );
            stop();
        }
        
        public long elapsedTime() { return (_stopTime - _startTime); }
        
    }
    
    public static abstract class MultiRun extends Test {
        
        int _runCount = 1;
        
        public MultiRun() { }
        
        public MultiRun( String description ) {
            this( description , 1 );
        }
        
        public MultiRun( String description, int runCount ) {
            super( description );
            setRunCount( runCount );
        }
        
        protected int runCount() { return _runCount; }
        protected void setRunCount( int count ) { _runCount = count; }
        
        protected long elapsed() { return super.elapsed() / runCount(); }
        
        /**
         * Run once with logging on, and then shot logging off before the loop starts.
         */
        public void test() throws Exception {
            trace( this, "Running " + runCount() + " times." );
            singleRun();
            logger().alterMute(true);
            for ( int i=1; i < runCount(); i++ ) {
                singleRun();
            }
            logger().restoreMute();
        }
        
        public abstract void singleRun();
        
    }
    
    public static class MRun extends MultiRun {
        
        private Test _target;
        
        public MRun( Test target ) {
            this( target, 1 );
        }
        
        public MRun( Test target, int runCount ) {
            super( target.description() );
            setTarget( target );
            setRunCount( runCount );
        }

        public Test target() { return _target; }
        public void setTarget( Test target ) { _target = target; }
        
//        protected long elapsed() { return super.elapsed() * runCount(); }
        
        public String description() {
            return "MultiRun[" + runCount() + "] of " + target().description();
        }
        
        public void init() {
            target().init();
        }
        
        public void test() throws Exception {
            trace( this, "Running " + runCount() + " times." );
            logger().alterMute(true);
            chronoTest( "multiRun", description() );
            logger().restoreMute();
        }
        
        public void tearDown() {
            target().tearDown();
        }

        public void multiRun() throws Exception {
            for ( int i=0; i < runCount(); i++ ) {
                target().test();
            }
        }
        
        public void singleRun() { }
    }
    
    public static abstract class AlternateRun extends MultiRun {
        
        private boolean _runAlternate = false;
        private boolean _runRegular = true;
        
        public AlternateRun() { }
        
        public AlternateRun( String description ) {
            this( description, 1 );
        }
        
        public AlternateRun( String description, int runCount ) {
            super( description, runCount );
        }
        
        public AlternateRun( String description, boolean reg, boolean alt ) {
            super( description );
            setRegular( reg );
            setAlternate( alt );
        }
        
        public abstract void runRegular();
        
        public abstract void runAlternate();
        
        protected String regularMsg() { return "Regular run"; }
        
        protected String alternateMsg() { return "Alternate run"; }
        
        public void singleRun() {
            if ( isRegular() )
                chronoTest( "runRegular", regularMsg(), null );
            if ( isAlternate() )
                chronoTest( "runAlternate", alternateMsg(), null );
        }
        
        public String description( boolean alt ) {
            return ( alt ) ? alternateMsg() : regularMsg();
        }
        
        public boolean isAlternate() { return _runAlternate; }
        
        public void setAlternate(boolean alternate) {
            _runAlternate = alternate;
        }
        
        public boolean isRegular() {
            return _runRegular;
        }
        
        public void setRegular(boolean regular) {
            _runRegular = regular;
        }
        
    }
    
}














