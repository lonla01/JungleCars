

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */
package com.camcars.test;

import com.camcars.*;
import java.util.*;

public class TestSuite extends PSObject {
    
    private String _description;
    private PSArray _tests;
    private PSArray _testSuites;
    private static PSLogger _logger;

    public TestSuite( String description ) {
        _description = description;
        _tests = new PSArray();
        _testSuites = new PSArray();
    }

    // This is required by the parent PSObject which expect all subclasses to
    // have persistence ability
    public void takePropertyDict( PSDict dict ) {}

    public String description() {
        return _description;
    }

    public void addTest( Test test ) {
        test.setTestSuite(this);
        _tests.addElement( test );
    }

    public void addTestSuite( TestSuite testSuite ) {
        _testSuites.addElement( testSuite );
    }

    public void runTestSuites() {
        Iterator testSuiteIter = _testSuites.iterator();
        while (testSuiteIter.hasNext()) {
            TestSuite aTestSuite = (TestSuite )testSuiteIter.next();
            // We use logger().error() so that the string appears red in JBuider log
            logger().error( "\nTestSuite: " + aTestSuite.description() );
            aTestSuite.runTests();
        }
    }

    public void runTests() {
        Iterator testIter = _tests.iterator();
        while (testIter.hasNext()) {
            Test aTest = (Test )testIter.next();
            _runTest( aTest );
        }
    }

    private void _runTest( Test aTest ) {
        long start = 0, end = 0;

        try {
            aTest.init();
            start = System.currentTimeMillis();
            aTest.test();
            end = System.currentTimeMillis();
            aTest.setElapsed( end - start );
            aTest.setSuccess();
            logger().log( "\n" );
        } catch ( PSAssert.AssertionFailure err ) {
            end = System.currentTimeMillis();
            logger().error( "Failure: " + err.getMessage() );
            aTest.setElapsed( end - start );
            aTest.setFailure( err );
        } catch (Throwable e) {
            end = System.currentTimeMillis();
            logger().error( "Error: " + e.toString(), e );
            aTest.setElapsed( end - start );
            aTest.setError( e );
        } finally {
            aTest.tearDown();
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        // Print children test suites
        Iterator testIter = _testSuites.iterator();
        while (testIter.hasNext()) {
            TestSuite aTest = (TestSuite )testIter.next();
            str.append( aTest.toString() );
            str.append("\n");
        }

        // Print children test cases
        testIter = _tests.iterator();
        while (testIter.hasNext()) {
            Test aTest = (Test )testIter.next();
            str.append( aTest.toString() );
            str.append("\n");
        }

        return str.toString();
    }
    
    /**
     * We pass a shared logger object to the Model
     * This should be done very early because the logger object is shared by
     * the rest of the code. The model classes descending from PSObject as well
     * as the UI classes descending from PSPanel or PSFrame
     */
    private static void installLogger() {
      _logger = new PSLogger();
      _logger.setDebug(true);
      _logger.setTrace(true);
      _logger.setVerbose(false);
      _logger.setStackTracing(true);      
      _logger.setMute(false);
      
      PSObject.setLogger(_logger);
    }
    
    public static void ___main(String[] args) {
    	
        installLogger();
        PSObject.setDataStoreDir( "TestStore/" );
        PSID.Generator.setResetCounting(true);
        try {
        	
            TestSuite parentSuite = new TestSuite( "Root test suite" );
            //logger().log( "Starting Unit Tests" );
            //parseArguments( args );
            
            parentSuite.addTestSuite( new DBImportTestSuite() );

            parentSuite.runTestSuites();
            
            logger().alterMute(false);
            logger().shout( parentSuite.toString() );
            logger().restoreMute();
            
        } catch (Exception e) {
            logger().error( "Error while running test", e );
        }
    }

//    public static void parseArguments(String[] args) {
//        for( int i=0; i < args.length; i++) {
//            String myArg = args[i];
//            if ( "-if".equals(myArg) || "-bibleImportFile".equals(myArg) ) {
//                _PersistenceTestSuite.TextImportTest.setDocImportFile(args[++i]);
//            }  else if ( "-bv".equals(myArg) || "-bibleVersion".equals(myArg) ) {
//                _PersistenceTestSuite.TextImportTest.setDocVersion(args[++i]);
//            } else if ( "-cc".equals(myArg) || "-inputCharCount".equals(myArg) ) {
//                _PersistenceTestSuite.TextImportTest.setInputCharCount(args[++i]);
//            } else if ( "-vc".equals(myArg) || "-bibleVerseCount".equals(myArg) ) {
//                _PersistenceTestSuite.TextImportTest.setDocEntryCount(args[++i]);
//            }
//        }
//    }

}

















