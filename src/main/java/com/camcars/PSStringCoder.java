
package com.camcars;

import java.io.File;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
/**
 * PSStringCoder.java
 *
 * Created on 20 ao�t 2009, 20:23
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public class PSStringCoder extends PSPlistCoder {
    
    public static final PSString _SkipChars  = new PSIndexedString( SKIP_CHARS );
    public static final PSString _Delimiters = new PSIndexedString( DELIMITERS ); 
    private static PSTokenizer _ClassTokenizer = null;
    
    public PSStringCoder() { super(); }
    
    public PSStringCoder( File path ) { super( path ); }
    
    public void initForReading( Object plist ) {
        setReader( new Reader( (String )plist ) );
    }
    
    public static class Reader extends PSPlistCoder.Reader {
        
        private PSTokenizer _tokenizer      = null;
        private PSString    _plistString    = null;        
        private PSDict      _processingMap  = null;
        
        public Reader() {
            this( (PSString )null );
        }
        
        public Reader( String plistString ) {
            super( plistString );
            setPlistString( plistString );
            processingMap();
        }

        public Reader( PSString plistString ) {
            super( plistString.toString() );
            setPSString( plistString );
            processingMap();
        }
                
        protected PSDict processingMap() {
            if ( _processingMap == null ) {
                _processingMap = new PSDict(20);
                _processingMap.put( new PSArrayString( ESCAPE               ), new EscapeChar()         );
                _processingMap.put( new PSArrayString( STRING_START         ), new StringStart()        );
                _processingMap.put( new PSArrayString( INTEGER_START        ), new IntegerStart()       );
                _processingMap.put( new PSArrayString( FLOAT_START          ), new FloatStart()         );
                _processingMap.put( new PSArrayString( BOOLEAN_START        ), new BooleanStart()       );
                _processingMap.put( new PSArrayString( PSOBJECT_START       ), new PSObjectStart()      );
                _processingMap.put( new PSArrayString( DICT_START           ), new DictStart()          );
                _processingMap.put( new PSArrayString( KEY_VALUE_SEPARATOR  ), new KeyValueSeparator()  );
                _processingMap.put( new PSArrayString( ARRAY_START          ), new ArrayStart()         );
                _processingMap.put( new PSArrayString( DICT_ENTRY_SEPARATOR ), new DictSeparator()      );
                _processingMap.put( new PSArrayString( ARRAY_SEPARATOR      ), new ArraySeparator()     );
                _processingMap.put( new PSArrayString( PSOBJECT_END         ), new PSObjectEnd()        );
                _processingMap.put( new PSArrayString( DICT_END             ), new DictEnd()            );
                _processingMap.put( new PSArrayString( ARRAY_END            ), new ArrayEnd()           );
            }
            
            return _processingMap;
        }

        public void resetReading() {
            super.resetReading();
            setPSString( null );
        }
        
        ///   A C C E S S O R S   //////////////////////////////////////////////
        protected PSTokenizer tokenizer() { return _tokenizer; }
        protected void setTokenizer( PSTokenizer tokenizer ) {
            _tokenizer = tokenizer;
        }
        
        public void setPlistString( String aPlist ) {
            if ( aPlist != null )
                setPSString( new PSArrayString( aPlist ) );
        }
        
        protected PSString psString() { return _plistString; }
        protected void setPSString( PSString aString ) {
            _plistString = aString;
        }
        
        protected TokProc tokenProcessor() {
            return (TokProc )processingMap().get( token() );
        }

        protected Map createDict() {
            try {
                return (Map )stackDictClass().newInstance();
            } catch (Exception e) {
                throw new PSPlistCoder.PlistException( "Invalid Dictionary class" );
            }
        }
        
        public PSString delimiters() {
            return _Delimiters;
        }
        
        public static final PSString skipChars() {
            return _SkipChars;
        }
        
        protected Object nextToken() {
            Object token = tokenizer().nextToken();
            
            return token;
        }
        
        protected PSInt createInt( Object token ) {
            return PSInt.parsePSInt( (PSString )token );
        }
        
        protected boolean hasMoreTokens() {
            return tokenizer().hasMoreTokens();
        }
        
        private static PSTokenizer classTokenizer( 
                PSString str,
                PSString delims,
                boolean returnDelims ) {
            
            if ( _ClassTokenizer == null ) {
                _ClassTokenizer = new PSTokenizer( str, delims, returnDelims );
            } else {
                _ClassTokenizer.resetState( str, delims, returnDelims );
            }
            
            return _ClassTokenizer;
        }
        
        ///   D E C O D I N G   ////////////////////////////////////////////////
        public Object decodeObject() {            
            setTokenizer(
                classTokenizer(
                    psString(),
                    delimiters(),
                    DELIM_ARE_TOKEN
                )
            );
            try {
                while ( hasMoreTokens() ) {
                    _token = (PSTokenizer.Token )nextToken();

                    if ( ( (PSString )token() ).length() > 3 )
                        logger().verbose( this, "Token=[" + token() + "]" );

                    TokProc tokenProcessor = (TokProc )processingMap().get( _token );                    
                    if ( tokenProcessor != null ) {
                        tokenProcessor.processToken();
                    } else {
                        commonString(); 
                    }
                }
            } catch( Throwable e ) {
                logger().error( this, "Error processing token: [" + token() + "]", e );
            }
            
            Object original = originalObject( returnValue() );
                                    
            return original;
        }

        protected boolean shouldSkipToken( Object token ) {
            PSString tokString = (PSString )token;
            
            if ( tokString.length() != 1 ) return false;
            
            return ( skipChars().indexOf( tokString.charAt(0) ) >= 0 );
        }

        ///   I N N E R   C L A S S E S   //////////////////////////////////////
        
        abstract class TokProc { abstract void processToken(); }
        class EscapeChar        extends TokProc { void processToken() { escapeChar();       } }
        class StringStart       extends TokProc { void processToken() { stringStart();      } }
        class IntegerStart      extends TokProc { void processToken() { integerStart();     } }
        class FloatStart        extends TokProc { void processToken() { floatStart();       } }
        class BooleanStart      extends TokProc { void processToken() { booleanStart();     } }
        class PSObjectStart     extends TokProc { void processToken() { psObjectStart();    } }
        class DictStart         extends TokProc { void processToken() { dictStart();        } }
        class KeyValueSeparator extends TokProc { void processToken() { keyValueSeparator();} }
        class ArrayStart        extends TokProc { void processToken() { arrayStart();       } }
        class DictSeparator     extends TokProc { void processToken() { dictSeparator();    } }
        class ArraySeparator    extends TokProc { void processToken() { arraySeparator();   } }
        class PSObjectEnd       extends TokProc { void processToken() { psObjectEnd();      } }
        class DictEnd           extends TokProc { void processToken() { dictEnd();          } }
        class ArrayEnd          extends TokProc { void processToken() { arrayEnd();         } }
        
    }  /// End Reader.

    public static class Writer extends PSPlistCoder.Writer {

        private static int _IndentLevel = 0;
        private static final PSString INDENT_STRING = PSString.with( "        " );
        private static PSStringCoder.Writer.ArrayWriter _ArrayWriter = null;
        private static PSStringCoder.Writer.DictWriter  _DictWriter = null;
        private static PSStringCoder.Writer.ArrayEntryWriter _ArrayEntryWriter = null;
        private static PSStringCoder.Writer.DictEntryWriter _DictEntryWriter = null;

        public Writer() {
            super(null);
            // This removes the ^# before PSInt representations
            setIsSnapshotEncoding( true );
            // All strings should be quoted. This prevent any weirdness for strings
            // containing special chars like: '!'.
//            setAllwaysUseDelimiters( true );
        }

//        public static boolean isSnapshotEncoding() {
//            return true;
//        }

        public void encodeObject( Object anObject ) {
            logger().trace( this, "Creating object tree..." );
            long t1 = System.currentTimeMillis();
            _encodeObject(anObject);
            long t2 = System.currentTimeMillis();
            logger().trace( this, "finished _encodeObject()" );
            long t3 = System.currentTimeMillis();
            logger().trace(this, "Rendering plist string done. Graph="+(t2-t1) +"ms Rendering="+(t3-t2)+"ms" );
        }

        public static void encodeNestedArray( PSBuffer buffer, Collection array ) {
            PSPlistCoder.Writer.ArrayWriter nestedWriter =
                            (PSPlistCoder.Writer.ArrayWriter )pool().plistWriterOfClass(
                            PSStringCoder.Writer.ArrayWriter.class, null, null );
            nestedWriter.setBuffer( buffer );
            nestedWriter.setTarget( array );
            nestedWriter.encodeArray();
        }

        public static void encodeNestedDict( PSBuffer buffer, Map dict ) {
            PSPlistCoder.Writer.DictWriter nestedWriter =
                            (PSPlistCoder.Writer.DictWriter )pool().plistWriterOfClass(
                            PSStringCoder.Writer.DictWriter.class, null, null );
            nestedWriter.setBuffer( buffer );
            nestedWriter.setTarget( dict );
            nestedWriter.encodeDict();
        }

        protected static void performIndent(PSBuffer buffer, int indentLevel ) {
            buffer.append( NEW_LINE );
            for ( int i = 0; i < indentLevel; i++ ) {
                buffer.append( INDENT_STRING );
            }
        }

        private static PSStringCoder.Writer.ArrayWriter arrayWriter() {
            if ( _ArrayWriter == null ) {
                _ArrayWriter = (PSStringCoder.Writer.ArrayWriter )pool().plistWriterOfClass(
                        PSStringCoder.Writer.ArrayWriter.class, null, null );
            }

            return _ArrayWriter;
        }

        private static PSStringCoder.Writer.DictWriter dictWriter() {
            if ( _DictWriter == null ) {
                _DictWriter = (PSStringCoder.Writer.DictWriter )pool().plistWriterOfClass(
                    PSStringCoder.Writer.DictWriter.class, null, null );
            }

            return _DictWriter;
        }

        private static PSStringCoder.Writer.ArrayEntryWriter arrayEntryWriter() {
            if ( _ArrayEntryWriter == null ) {
                _ArrayEntryWriter = (PSStringCoder.Writer.ArrayEntryWriter )pool().plistWriterOfClass(
                        PSStringCoder.Writer.ArrayEntryWriter.class, null, null );
            }

            return _ArrayEntryWriter;
        }

        private static PSStringCoder.Writer.DictEntryWriter dictEntryWriter() {
            if ( _DictEntryWriter == null ) {
                _DictEntryWriter = (PSStringCoder.Writer.DictEntryWriter )pool().plistWriterOfClass(
                        PSStringCoder.Writer.DictEntryWriter.class, null, null );
            }

            return _DictEntryWriter;
        }

        protected PSPlistCoder.Writer encodeArray( Collection array ) {
            PSPlistCoder.Writer.ArrayWriter aWriter = arrayWriter();
            aWriter.setBuffer( buffer() );
            aWriter.setTarget( array );
            aWriter.encodeArray();
            return this;
        }

        public void encodeDict( Object dict ) {
            PSPlistCoder.Writer.DictWriter aWriter = dictWriter();
            aWriter.setBuffer( buffer() );
            aWriter.setTarget( dict );
            aWriter.encodeDict();
        }

        ///   N E S T E D   C L A S S E S   ///////////////////////////////////////////////////
        public static class ArrayWriter extends PSPlistCoder.Writer.ArrayWriter {
            
            protected void appendArrayEntry( Object anEntry ) {
                PSPlistCoder.Writer.ArrayEntryWriter aWriter = arrayEntryWriter();
                aWriter.setBuffer( buffer() );
                aWriter.setTarget( anEntry );

                arraySeparator();
                indent();
                aWriter.encodeArrayEntry();
            }

            protected PSArray psArray() { return (PSArray )array(); }

            protected void arrayStart() {
                super.arrayStart();
                _IndentLevel++;
            }

            protected void arrayEnd() {
                _IndentLevel--;
                indent();
                super.arrayEnd();
            }

            public void indent() {
                if ( shouldIndent() )
                    performIndent( buffer(), _IndentLevel );
            }

            public boolean shouldIndent() {
                if ( psArray().size() > 0 && psArray().firstElement() instanceof Map ) {
                    return true;
                }
                return array().size() > 2;
            }

        } /// End ArrayWriter;       

        public static class ArrayEntryWriter extends PSPlistCoder.Writer.ArrayEntryWriter {

            protected void encodeArrayEntry() {
                if ( entry() instanceof String ) {
                    encodeString( (String )entry() );
                } else if ( entry() instanceof PSRoot.Appending ) {
                    PSRoot.Appending anEntry = (PSRoot.Appending )entry();
                    anEntry.appendTo( buffer() );
                } else if ( entry() instanceof Collection ) {
                    encodeNestedArray( buffer(), (Collection )entry() );
                } else if ( entry() instanceof Map ) {
                    encodeNestedDict( buffer(), (Map )entry() );
                 } else {
                        super.encodeArrayEntry();
                 }
            }

        } /// End ArrayEntryWriter.

        public static class DictWriter extends PSPlistCoder.Writer.DictWriter {

            protected void appendDictEntry( Object aKey, Object aValue ) {
                PSPlistCoder.Writer.DictEntryWriter aWriter = dictEntryWriter();
                aWriter.setBuffer( buffer() );
                aWriter.setKey( aKey );
                aWriter.setValue( aValue );
                indent();
                aWriter.encodeDictEntry();
                dictSeparator();
            }

            private PSDict psDict() { return (PSDict )dict(); }

            protected void dictStart() {
                super.dictStart();
                _IndentLevel++;
            }

            protected void dictEnd() {
                _IndentLevel--;
                indent();
                super.dictEnd();
            }

            public void indent() {
                if ( shouldIndent() )
                    performIndent( buffer(), _IndentLevel );
            }

            public boolean shouldIndent() {
                return psDict().size() > 2;
            }

        } /// End DictWriter.

        public static class DictEntryWriter extends PSPlistCoder.Writer.DictEntryWriter {

            private void encodeKey() {
                if ( key() instanceof String ) {
                    super.encodeString( (String )key() );
                } else if ( key() instanceof PSRoot.Appending ) {
                    PSRoot.Appending aKey = (PSRoot.Appending )key();
                    aKey.appendTo( buffer() );
                } else {
                    _encodeObject( key() );
                }
            }

            private void encodeValue() {
               if ( value() instanceof String ) {
                    encodeString( (String )value() );
                } else if ( value() instanceof PSRoot.Appending ) {
                    PSRoot.Appending aValue = (PSRoot.Appending )value();
                    aValue.appendTo( buffer() );
                } else if ( value() instanceof Collection ) {
                    encodeNestedArray( buffer(), (Collection )value() );
                } else if ( value() instanceof Map ) {
                    encodeNestedDict( buffer(), (Map )value() );
                 } else {
                    _encodeObject( replacementObject( value() ) );
                }
            }

            public PSPlistCoder.Writer encodeString( String aString ) {
                buffer().append( STRING_START );
                buffer().append( escapeSpecChars( aString ) );
                buffer().append( STRING_END );

                return this;
            }

            protected void encodeDictEntry() {
                encodeKey();
                appendConditionalSpace();
                buffer().append( KEY_VALUE_SEPARATOR );
                appendConditionalSpace();
                encodeValue();               
            }
        }

    } /// End Writer.
    
}
