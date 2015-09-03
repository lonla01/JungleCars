

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
import java.io.File;
import java.util.*;

public class PSPlistCoderTestSuite  extends TestSuite {

    Collection _simple_array, _complex_array, _very_complex_array;
    Collection _array_with_spaces, _array_with_spec_char;
    Map _simple_dict, _complex_dict, _fairly_complex_dict, _very_complex_dict;
    Map _dict_with_spaces, _dict_with_spec_char;

    static final String EMPTY_ARRAY_PLIST = "( )";
    static final String EMPTY_DICT_PLIST = "{ }";
    static final String SIMPLE_ARRAY_PLIST = "(One, Two, Three)";
    static final String COMPLEX_ARRAY_PLIST =
            "( One, Two, Three, ( A, B, C ) )";
    static final String VERY_COMPLEX_ARRAY_PLIST =
            "( One, Two, Three, ( A, B, C ), { #^1 = One; #^2 = Two; #^3 = Three; } )";
    static final String SIMPLE_DICT_PLIST =
            "{ #^1 = One; #^2 = Two; #^3 = Three; }";
    static final String COMPLEX_DICT_PLIST =
            "{ #^1 = One; #^2 = Two; #^3 = Three; #^4 = ( One, Two, Three ); }";
    static final String VERY_COMPLEX_DICT_PLIST =
            "{ #^1 = One; #^2 = Two; #^3 = Three; #^4 = ( One, Two, Three ); #^5 = { #^1 = One; #^2 = Two; #^3 = Three; }; }";
    static final String FAIRLY_COMPLEX_DICT_PLIST =
            "{ #^5 = { #^1 = One; #^2 = Two; #^3 = Three; }; }";
    
    static final String STRING_WITH_SPACES = "String with space";
    static final String STRING_WITH_MORE_SPACES = "\t   Another   \nString with more spaces";
    static final String STRING_WITH_SPEC_CHAR = "\t   [Another   \nString] {with more spaces";
    static final String ESCAPED_STRING_WITH_SPEC_CHAR = "\t   \\[Another   \nString\\] \\{with more spaces";
    static final String CONTENT_OBJECT_PLIST = "[PSMetaContent {entriesLineCount=#^23290;links=();refs=();entryCount=#^3927;tokenCount=#^1105267;plistClass=[PSCustomCoder$ClassCoder {class=PSStringCoder;}];importLineCount=#^23290;gID=[PSID$Coder {k=#^3;}];releaseYear=#^-1;linkCount=#^652;parserClass=[PSCustomCoder$ClassCoder {class=PSParser$PlistDictParser;}];lineCount=#^23290;language=EN;importerClass=[PSCustomCoder$ClassCoder {class=PSParser$EastonImporter;}];desc=\"The Easton's Bible Dictionary\";storage=Dictionaries;cits=();fun=\"Easton Bible Dictionary\";name=Easton;}]";

    static final String ARRAY_WITH_SPACES_PLIST =
            "( \"" + STRING_WITH_SPACES + "\", \"" + STRING_WITH_MORE_SPACES + "\" )";
    static final String ARRAY_WITH_SPEC_CHAR_PLIST =
            "( \"" + STRING_WITH_SPACES + "\", \"" + STRING_WITH_MORE_SPACES + "\", \"" +
            ESCAPED_STRING_WITH_SPEC_CHAR + "\" )";
    static final String DICT_WITH_SPACES_PLIST =
            "{ #^1 = \"" + STRING_WITH_SPACES + "\"; #^2 = \"" + STRING_WITH_MORE_SPACES + "\"; }";
    static final String DICT_WITH_SPEC_CHAR_PLIST =
            "{ #^1 = \"" + STRING_WITH_SPACES + "\"; #^2 = \"" + STRING_WITH_MORE_SPACES +
            "\"; #^3 = \"" + STRING_WITH_SPEC_CHAR + "\"; }";
    static final String ESCAPED_DICT_WITH_SPEC_CHAR_PLIST =
            "{ #^1 = \"" + STRING_WITH_SPACES + "\"; #^2 = \"" + STRING_WITH_MORE_SPACES +
            "\"; #^3 = \"" + ESCAPED_STRING_WITH_SPEC_CHAR + "\"; }";


    public PSPlistCoderTestSuite() {
        super("Testing of plist generation and parsing...");

        addTest( new PlistWriterTest()      );
//        addTest( new PlistReaderTest()      );

//        addTest( new PlistWriterLoadTest() );
    }

    abstract class PlistTest extends Test {

        PSPlistCoder.Reader _reader = null;
        PSPlistCoder.Writer _writer = null;
        PSDict _objectTable  = null;
        PSDict _descTable = null;
        PSDict _plistTable    = null;

        public PlistTest( String desc ) {
            super( desc );
        }

        protected PSDict objectTable() {
            if ( _objectTable == null )
                _objectTable = new PSDict();

            return _objectTable;
        }

        protected PSDict descTable() {
            if ( _descTable == null )
                _descTable = new PSDict();

            return _descTable;
        }

        protected PSDict plistTable() {
            if ( _plistTable == null )
                _plistTable = new PSDict();

            return _plistTable;
        }

        protected void addTestItem( Object item, String shortDesc,
                String fullDesc, String plistEncoding ) {

            objectTable().put( shortDesc, item );
            descTable().put( shortDesc, fullDesc );
            plistTable().put( shortDesc, plistEncoding );
        }

        public PSPlistCoder.Reader reader() { return _reader; }
        public PSPlistCoder.Writer writer()  { return _writer; }

        public void resetReader() { _reader = _createReader( null ); }
        public void resetWriter()  { _writer = _createWriter();      }

        public String decodedPlist() { return writer().toString(); }

        public void init() {
            _simple_array = new Vector();
            _simple_array.add( "One" );
            _simple_array.add( "Two" );
            _simple_array.add( "Three" );
            addTestItem( _simple_array, "simple array",
                    "Simple array of Strings", SIMPLE_ARRAY_PLIST );

            _complex_array = new Vector( _simple_array );
            Collection abc = new Vector();
            abc.add( "A" );
            abc.add( "B" );
            abc.add( "C" );
            _complex_array.add( abc );
            addTestItem( _complex_array, "complex array",
                    "Complex array of Strings", COMPLEX_ARRAY_PLIST );

            _simple_dict = new TreeMap();
            _simple_dict.put( PSInt.with(3), "Three" );
            _simple_dict.put( PSInt.with(2), "Two" );
            _simple_dict.put( PSInt.with(1), "One" );
            addTestItem( _simple_dict, "simple dict",
                    "Simple dict of Strings", SIMPLE_DICT_PLIST );

            // We use ArrayList instead of Vector here because Collections.sort() doesn't accept Vectors
            _very_complex_array = new ArrayList(_complex_array);
            _very_complex_array.add( _simple_dict );
            addTestItem( _very_complex_array, "very complex array",
                    "array with array and dict element", VERY_COMPLEX_ARRAY_PLIST );

            _complex_dict = new TreeMap(_simple_dict);
            _complex_dict.put( PSInt.with(4), _simple_array);
            addTestItem( _complex_dict, "complex dict",
                    "dict with array value", COMPLEX_DICT_PLIST );
            
            _fairly_complex_dict = new TreeMap();
            _fairly_complex_dict.put( PSInt.with(5), _simple_dict );
            addTestItem( _fairly_complex_dict, "fairly complex dict",
                    "dict with dict value", FAIRLY_COMPLEX_DICT_PLIST );
                    
            _very_complex_dict = new TreeMap(_complex_dict);
            _very_complex_dict.put( PSInt.with(5), _simple_dict );
            addTestItem( _very_complex_dict, "very complex dict",
                    "dict with dict value", VERY_COMPLEX_DICT_PLIST );

            _array_with_spaces = new Vector();
            _array_with_spaces.add(STRING_WITH_SPACES);
            _array_with_spaces.add(STRING_WITH_MORE_SPACES);
            addTestItem( _array_with_spaces, "array with spaces",
                    "array with element containing spaces", ARRAY_WITH_SPACES_PLIST );

            _array_with_spec_char = new Vector(_array_with_spaces);
            _array_with_spec_char.add(STRING_WITH_SPEC_CHAR);
            addTestItem( _array_with_spec_char, "array with spec char",
                    "array with element containing special characters", ARRAY_WITH_SPEC_CHAR_PLIST );

            _dict_with_spaces = new TreeMap();
            _dict_with_spaces.put( PSInt.with(1), STRING_WITH_SPACES );
            _dict_with_spaces.put( PSInt.with(2), STRING_WITH_MORE_SPACES );
            addTestItem( _dict_with_spaces, "dict with spaces",
                    "dict with element containing spaces", DICT_WITH_SPACES_PLIST );

            _dict_with_spec_char = new TreeMap(_dict_with_spaces);
            _dict_with_spec_char.put( PSInt.with(3), STRING_WITH_SPEC_CHAR );
            addTestItem( _dict_with_spec_char, "escaped dict with spec char",
                    "dict with element containing special characters", ESCAPED_DICT_WITH_SPEC_CHAR_PLIST );

//            JCEntity aContent = dictStore().versionForName( "Easton" );
//            addTestItem( aContent, "content object", "object of PSContent as its super-class.", CONTENT_OBJECT_PLIST );
        }

        protected PSPlistCoder.Writer _createWriter() {
            if (_writer != null) {
                _writer.recycleAllInstances();
                pool().store().debugStats();
            }
            _writer = new PSPlistCoder.Writer();

            return _writer;
        }

        protected PSPlistCoder.Reader _createReader( String plist ) {
            return new PSPlistCoder.Reader( plist );
//            return new PSStringCoder.Reader( plist );
        }

        public void tearDown() {  }

    }   /// End PlistTest

    class PlistWriterTest extends PlistTest {

        public PlistWriterTest() {
            super("Testing plist generation on arrays and dicts");
        }

        public void performTest( String shortDesc ) {
            String fullDesc = (String )descTable().get( shortDesc );
            String storedPlist = (String )plistTable().get( shortDesc );
            Object testObject = objectTable().get( shortDesc );

            resetWriter();
            writer().encodeObject( testObject );
//            logger().trace( this, shortDesc + " = '" + testObject + "'" );
//            logger().trace( this, "'" + writer().toString() + "'" );
//            shout( this, "short=" + shortDesc + "full=" + fullDesc + " plist=" + storedPlist + " decoded=" + decodedPlist() );
            if ( storedPlist != null ) {
                String trimmedPlist = PSString.trimAllBlanks( storedPlist );
                String decodedPlist = PSString.trimAllBlanks( decodedPlist() );
                System.out.println( trimmedPlist );
                System.out.println( decodedPlist );
                assertEq( "Testing " + shortDesc, trimmedPlist, decodedPlist );
            }
        }

        public void test() {
            performTest( "simple array" );
            performTest( "complex array" );
            performTest( "very complex array" );
            performTest( "simple dict" );
            performTest( "complex dict" );
            performTest( "fairly complex dict" );
            performTest( "very complex dict" );
            performTest( "array with spaces" );
            performTest( "array with spec char" );
            performTest( "dict with spaces" );            
            writer().setIndentationEnabled(false);
            performTest( "escaped dict with spec char" );
            writer().setSpaceInsertion(false);
            performTest( "content object" );
        }

    } /// End PlistWriterTest


    class PlistReaderTest extends PlistTest {

        public PlistReaderTest() {
            super("Testing plist parsing on arrays and dicts");
        }

        public void test() throws Exception {
        	
            _reader = _createReader( EMPTY_ARRAY_PLIST );
            Collection empty_array = (Collection )_reader.decodeObject();
            logger().verbose( this,"empty_array = " + empty_array );
            logger().verbose( this,"EMPTY_ARRAY_PLIST = " + EMPTY_ARRAY_PLIST );
            assertTrue( "Empty array doesn't match expected value",
                        new Vector().equals( empty_array ) );

            _reader = _createReader( EMPTY_DICT_PLIST );
            Map empty_dict = (Map )_reader.decodeObject();
            logger().verbose( this,"empty_dict = " + empty_dict );
            logger().verbose( this,"EMPTY_DICT_PLIST = " + EMPTY_DICT_PLIST );
            assertTrue( "Empty dict doesn't match expected value",
                        new TreeMap().equals( empty_dict ) );

        }

    } /// End PlistReaderTest

    class JCDatabaseImport extends PlistTest {

        public JCDatabaseImport() {
            super("Testing plist parsing on arrays and dicts");
        }

        public void test() throws Exception {
        	
            _reader = _createReader( EMPTY_ARRAY_PLIST );
            Collection empty_array = (Collection )_reader.decodeObject();
            logger().verbose( this,"empty_array = " + empty_array );
            logger().verbose( this,"EMPTY_ARRAY_PLIST = " + EMPTY_ARRAY_PLIST );
            assertTrue( "Empty array doesn't match expected value",
                        new Vector().equals( empty_array ) );

            _reader = _createReader( EMPTY_DICT_PLIST );
            Map empty_dict = (Map )_reader.decodeObject();
            logger().verbose( this,"empty_dict = " + empty_dict );
            logger().verbose( this,"EMPTY_DICT_PLIST = " + EMPTY_DICT_PLIST );
            assertTrue( "Empty dict doesn't match expected value",
                        new TreeMap().equals( empty_dict ) );

        }

    } /// End JCDatabaseImport

}


















