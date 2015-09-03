
package com.camcars;

/**
 * PSPlistCoder.java
 *
 * Created on 10 juin 2009, 13:38
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

import java.io.File;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Was PSObject.PlistCoder.
 */
public  class PSPlistCoder extends PSParser implements PSCoding.Parsing {
    
    // TODO make the string start char changeable
    public static final String STRING_START = "\"";
    public static final String STRING_END = STRING_START;
    public static final String BOOLEAN_START = "^";
    public static final String INTEGER_START = "#";
    public static final String PSINT_START = INTEGER_START + BOOLEAN_START;
    public static final String LONG_START = INTEGER_START + INTEGER_START;
    public static final String FLOAT_START = "�";
    public static final String DOUBLE_START = FLOAT_START + FLOAT_START;
    public static final String PSOBJECT_START = "[";
    public static final String ARRAY_START = "(";
    public static final String ARRAY_SEPARATOR = ",";
    public static final String PSOBJECT_END = "]";
    public static final String ARRAY_END = ")";
    public static final String DICT_START = "{";
    public static final String DICT_ENTRY_SEPARATOR = ";";
    public static final String DICT_END = "}";
    public static final String KEY_VALUE_SEPARATOR = "=";
    
    public static final String SPECIAL_CHARS = "()[]{}<>,;=\"^#�";
    public static final String TRIGGER_QUOTE_CHARS = "!";
    public static final String NEW_LINE = "\n";
    public static final String SPACE = " ";
    public static final String ESCAPE = "\\";
//    public static final String SKIP_CHARS = SPACE + "\t\n\r\f\\";
    public static final String SKIP_CHARS = SPACE + "\t\n\r\f";
    public static final String DELIMITERS = SPACE + ESCAPE + SPECIAL_CHARS;
    public static final boolean DELIM_ARE_TOKEN = true;
    
    public static final String y    = "y";
    public static final String Y    = "Y";
    public static final String YES  = "YES";
    public static final String yes  = "yes";
    public static final String TRUE = "TRUE";
    public static final String true_ = "true";
    
    public static final NullValue NULL_VALUE = new NullValue();
    
    public PSPlistCoder() {
        super();
    }
    
    public PSPlistCoder( File path ) {
        super( path );
        logger().trace( this, "Created for file: " + path.getAbsolutePath() );        
    }
    
    protected PSPlistCoder.Writer writer() { return (PSPlistCoder.Writer )_writer(); }
    protected PSPlistCoder.Reader reader() { return (PSPlistCoder.Reader )_reader(); }
    
    public void initForWriting() {
        initForWriting( null );
    }
    
    public void initForWriting( Object data ) {
        setWriter( new Writer() );
        writer().setIndentationEnabled( indentationEnabled() );
    }
    
    public void initForReading( Object plist ) {
        setReader( new Reader( (String )plist ) );
    }
    
    public void encodeObject( Object anObject ) {
        assertFalse( "Cannot encode null object",  anObject == null );
        logger().debug( this, "Encoding a " + anObject.getClass().getName() );
        
        long t1 = System.currentTimeMillis();
        initForWriting();
        writer().encodeObject( anObject );
        
        long t2 = System.currentTimeMillis();
        
        logger().verbose( this, "Encoded string: " + _writer().toString() );
        textLoader().encodeObject( _writer().toString() );
        long t3 = System.currentTimeMillis();
        
        writer().finishedWriting();
        
        pool().resetRecycling();
        pool().store().debugStats();
        long t4 = System.currentTimeMillis();
        
        logger().trace( this, "Encode:"+(t2-t1)+"ms" + " Write:"+(t3-t2)+"ms" +
                " Recycle:"+(t4-t3)+"ms" );
        
    }
    
    public Object decodeObject() {
        Object retVal = null;
        
        String plist = (String )textLoader().decodeObject();
        if (plist != null && plist.length() > 0) {
            logger().debug( this, "Reading from file: " + filePath().getAbsolutePath() );
            initForReading( plist );
            retVal = _reader().decodeObject();
        }
        
        return retVal;
    }

    public void resetCoding() {
        super.resetCoding();
        if ( writer() != null )
            writer().resetWriting();
        if ( reader() != null )
            reader().resetReading();
    }
    
    ///////////////////////  I N D E N T A T I O N   ////////////////////////
    public static void setIndentationEnabled( boolean b ) {
        Writer.setIndentationEnabled(b);
    }
    
    public static boolean indentationEnabled() {
        return Writer.indentationEnabled();
    }
    
    public static void setIndentSize( int size ) {
        Writer.setIndentSize( size );
    }
    
    public static int indentSize() {
        return Writer.indentSize();
    }
    
    public static void setIndentThreshold( int t ) {
        Writer.setIndentThreshold( t );
    }
    
    public static int indentThreshold() {
        return Writer.indentThreshold();
    }       
    
   ///////////////////   I N N E R   C L A S S E S   //////////////////////////
    
    /**
     * IMPORTANT REMARK:
     * This nested class enclose inner classes. These inner classes are actually
     * subclasses of this one. When they call super(), they trigger a call to
     * Writer(). This call makes instance variable to initialize. So they lose
     * all value set after they previous initialization.
     *
     * This is why most instance variable here are declared static. So that their
     * value is not affected by sub-classes instance initialization.
     */
    public static class Writer extends PSCustomCoder.Replacer
            implements PSCoding.Writing {
        
        private PSBuffer _buffer;
//        private StringBuffer _buffer;
        private PSArray _children = null;
        
        private Object _target = null;
        private Writer _parent = null;
        private int _insertionPoint = 0;
        private int _offset = 0;
        
        private static boolean _indentationEnabled = true;
        private static int _indentSize = 4;
        private static int _indentThreshold = 120;
        private int _lineLength = -1;
        
        private static boolean _spaceInsertion = true;
        public static boolean _isSnapshotEncoding = false;
        public static boolean _allwaysUseDelimiters = false;
        
        public Writer() {
            this(null);
        }
        
        public Writer( Writer parent ) {
            resetWriting();
            setParent( parent );
        }
        
        protected void resetWriting() {
            resetBuffer();
            setParent( null );
            children().clear();
            setInsertionPoint( buffer().length() );
            setOffset(0);
        }
        
        protected Object target() { return _target; }
        protected void setTarget( Object t ) { _target = t; }
        
        /// C O D I N G /////////////////////////////////////////////////////////////
        protected PSBuffer buffer() { return _buffer; }
        protected void setBuffer( PSBuffer buffer ) {
            _buffer = buffer;
        }
        
        protected void __resetBuffer() {
            if ( _buffer == null ) {
                _buffer = pool().bufferInstance();            
                if ( _buffer.length() != 0 )
                    logger().error( this, "Got non-empty buffer from pool" );
            } else {
                _buffer.clearContent();
            }
        }
        
        protected void resetBuffer() {
            _buffer = new PSBuffer();
        }
        
        public void finishedWriting() {
            pool().store().debugStats();
//            logger().debug( this, "Before recycling: nodeCount=[" + nodeCount() + "]" );
            recycleAllInstances();
            pool().store().debugStats();
//            logger().debug( this, "After recycling: nodeCount=[" + nodeCount() + "]" );
            pool().recycleInstance( _buffer );
            _buffer = null;
        }
        
        protected Writer parent() { return _parent; }
        protected void setParent( Writer parent ) {
            _parent = parent;
            if ( parent() != null )
                setInsertionPoint( parent().buffer().length() );
        }
        public boolean isRoot() { return parent() == null; }
        public PSInt nodeCount() {
            return PSInt.with( 1 + children().sumWithNumAttribute( "nodeCount" ) );
        }
        
        protected int insertionPoint() { return _insertionPoint; }
        protected void setInsertionPoint( int i ) { _insertionPoint = i; }
        
        protected PSArray children() {
            if (_children == null) {
                _children = new PSArray(5);
            }
            return _children;
        }
        protected void addChild( Writer child ) { _children.add( child ); }
        protected void setChildren( PSArray anArray ) { _children = anArray; }
        
        protected int offset() {  return _offset; }
        protected void setOffset( int offset ) { _offset = offset; }
        
        ///   <editor-fold defaultstate="collapsed" desc="I N D E N T A T I O N">
        public static boolean indentationEnabled() { return _indentationEnabled; }
        
        public static void setIndentationEnabled( boolean b ) {
            _indentationEnabled = b;
        }
        
        public static int indentSize() { return _indentSize; }
        public static void setIndentSize( int size ) { _indentSize = size; }
        public static int indentThreshold() { return _indentThreshold; }
        
        public static void setIndentThreshold( int threshold ) {
            _indentThreshold = threshold;
        }
        
        public boolean spaceInsertion() { return _spaceInsertion; }
        public void setSpaceInsertion( boolean bool ) { _spaceInsertion = bool; }
        
        protected void appendConditionalSpace() {
            if ( _spaceInsertion )
                appendSpace();
        }
        
        protected void appendSpace() {
            buffer().append( " " );
        }
        
        protected boolean needsIndentation() {
            if (_parent == null)
                return false;
            else
                return lineLength() > indentThreshold();
        }
        
        protected int lineLength() {
            if (_lineLength == -1) {
                _lineLength = 0;
                
                _lineLength +=  _buffer.length();
                for (int i=0; i < _children.size(); i++) {
                    Writer aChild = (Writer )_children.get(i);
                    _lineLength += aChild.lineLength();
                }
            }
            
            return _lineLength;
        }
        
        protected int indentationLength() {
            int retValue = (_parent == null) ? 0 : _parent.indentationLength();
            
            if (needsIndentation())
                retValue += _indentSize;
            
            return retValue;
        }
        
        protected void indent() {
            parent().insertString( insertionPoint(), "\n" );
            for ( int i=0; i < _parent.indentationLength(); i++ ) {
                parent().insertString( insertionPoint(), " " );
            }
        }
        
        protected boolean shouldIndent() {
            return _indentationEnabled && needsIndentation();
        }
        
        /// </editor-fold>
        
        /// <editor-fold defaultstate="collapsed" desc="N O N  -  P R I M I T I V E S">
        
//        protected Writer encodeArray( Collection array ) {
//            addChild( new ArrayWriter( this, array ) );
//            return this;
//        }
//
//        protected void encodeDict( Dictionary dict ) {
//            addChild( new DictWriter( this, dict ) );
//        }
//
//        protected void encodeDict( Map dict ) {
//            addChild( new DictWriter( this, dict ) );
//        }
//
//        protected void encodePSKeyValueCoding( PSCoding.KeyValueCoding psObject ) {
//            addChild( new PSKeyValueCodingWriter( this, psObject ) );
//        }
        
        protected Writer encodeArray( Collection array ) {
            ArrayWriter writer =  (ArrayWriter )pool().plistWriterOfClass(
                    ArrayWriter.class, this, array );
            addChild( writer );
            writer.encodeArray();
            return this;
        }

        public void encodeDict( Object aDict ) {
            asserter().assertTrue( "Wrong dict class", aDict instanceof Map ||
                    aDict instanceof Dictionary );
            DictWriter writer = (DictWriter )pool().plistWriterOfClass(
                    DictWriter.class, this, aDict );
            addChild( writer );
            writer.encodeDict();
       }


//        private void _encodeDictionary( Dictionary dict ) {
//            DictWriter writer = (DictWriter )pool().plistWriterOfClass(
//                    DictWriter.class, this, dict );
//            addChild( writer );
//            writer.encodeDict();
//        }
//
//        private void _encodeMap( Map dict ) {
//            DictWriter writer = (DictWriter )pool().plistWriterOfClass(
//                    DictWriter.class, this, dict );
//            addChild( writer );
//            writer.encodeDict();
//        }
        
        protected void encodePSKeyValueCoding( PSCoding.KeyValueCoding psObject ) {
            if ( psObject instanceof JCEntity ) {
                logger().verbose( this, "Encoding " + (JCEntity )psObject );
            }
            
            PSKeyValueCodingWriter writer = (PSKeyValueCodingWriter )pool().plistWriterOfClass(
                    PSKeyValueCodingWriter.class, this, psObject );
            addChild( writer );
            writer.encodePSKeyValueCoding();
//            writer.appendToParent();
        }
        
        /// </editor-fold>   E N D   N O N  -  P R I M I T I V E S
        
        /// <editor-fold defaultstate="collapsed" desc="P R I M I T I V E S">
        
        protected Writer encodeNumber( Number aNumber ) {
            if (aNumber instanceof Integer)
                encodeInteger( (Integer )aNumber );
            else if (aNumber instanceof Long)
                encodeLong( (Long )aNumber );
            else if (aNumber instanceof Float)
                encodeFloat( (Float )aNumber );
            else if (aNumber instanceof Double)
                encodeDouble( (Double )aNumber );
            else
                throw new PlistException( "Got an unsupported Number type" );
            
            return this;
        }
        
        protected Writer encodeInteger( Integer aNumber ) {
            buffer().append( INTEGER_START );
            buffer().append( aNumber );
            return this;
        }

        public static boolean isSnapshotEncoding() { return _isSnapshotEncoding; }
        public static void setIsSnapshotEncoding( boolean bool ) {
            _isSnapshotEncoding = bool;
        }

        public static boolean allwaysUseDelimiters() { return _allwaysUseDelimiters; }
        public static void setAllwaysUseDelimiters( boolean bool ) {
            _allwaysUseDelimiters = bool;
        }
        
        protected Writer encodePSInt(PSInt aNumber) {
            if ( ! isSnapshotEncoding() ) {
                buffer().append(PSINT_START);
            }
            buffer().append(aNumber);
            return this;
        }
        
        protected Writer encodeLong( Long aNumber ) {
            buffer().append( LONG_START );
            buffer().append( aNumber );
            return this;
        }
        
        protected Writer encodeFloat( Float aNumber ) {
            buffer().append( FLOAT_START );
            buffer().append( aNumber );
            return this;
        }
        
        protected Writer encodeDouble( Double aNumber ) {
            buffer().append( DOUBLE_START );
            buffer().append( aNumber );
            return this;
        }
        
        protected Writer encodeBoolean( Boolean aBoolean ) {
            buffer().append( BOOLEAN_START );
            buffer().append( aBoolean.booleanValue() ? "YES" : "NO" );
            return this;
        }

        protected boolean stringContainsChar( String aString, String chars ) {
           for (int i=0; i < chars.length(); i++) {
                char c = chars.charAt(i);
                if ( aString.indexOf(c) >= 0)
                    return true;
            }
            return false;
        }

        protected boolean shouldQuoteString( String aString ) {
            if ( allwaysUseDelimiters() ) return true;
            if ( aString.length() == 0 ) return true;
            return stringContainsChar( aString, SKIP_CHARS ) ||
                    stringContainsChar( aString, TRIGGER_QUOTE_CHARS );
        }
        
        protected String escapeSpecChars( String  aToken ) {
            PSBuffer buffer = pool().bufferInstance();
            StringTokenizer aTokenizer = new StringTokenizer( aToken, SPECIAL_CHARS, true );
            
            while( aTokenizer.hasMoreTokens() ) {
                String myToken = aTokenizer.nextToken();
                if ( isSpecialChar(myToken) )
                    buffer.append("\\");
                buffer.append(myToken);
            }
            String result = buffer.toString();
            pool().recycleInstance( buffer );
            
            return result;
        }
        
        protected boolean isSpecialChar( String aToken ) {
            if ( aToken == null || aToken.length() != 1 ) return false;
            return SPECIAL_CHARS.indexOf( aToken ) >= 0;
        }
        
        public Writer encodeString( String aString ) {
            if ( shouldQuoteString( aString ) ) {
                buffer().append( STRING_START );
                buffer().append( escapeSpecChars( aString ) );
                buffer().append( STRING_END );
            } else
                buffer().append( aString );
            
            return this;
        }
        
        /// </editor-fold>  P R I M I T I V E S
        
        public void encodeObject( Object anObject ) {                        
            logger().trace( this, "Creating object tree..." );
            long t1 = System.currentTimeMillis();
            _encodeObject(anObject);
            long t2 = System.currentTimeMillis();
            logger().trace( this, "finished _encodeObject()" );
            appendToParent();
            logger().trace( this, "finished appendToParent()" );
            long t3 = System.currentTimeMillis();
            logger().trace(this, "Rendering plist string done. Graph="+(t2-t1) +"ms Rendering="+(t3-t2)+"ms" );         
        }
        
        protected void _encodeObject( Object anObject ) {
            Object replacement;
                        
            if (anObject == null) {
                this._encodeObject( NULL_VALUE );                
            } else if (anObject instanceof String) {
                this.encodeString( (String )anObject );
            } else if (anObject instanceof PSInt) {
                this.encodePSInt( (PSInt )anObject );
            } else if (anObject instanceof Number) {
                this.encodeNumber( (Number )anObject );
            } else if (anObject instanceof Boolean) {
                this.encodeBoolean( (Boolean )anObject );
            } else if ( isCollection( anObject ) ) {
                this.encodeArray( (Collection )anObject );
            } else if (anObject instanceof Dictionary || anObject instanceof Map) {
                this.encodeDict( anObject  );
            } else if ( isKeyValueCoding( anObject ) ) {
                this.encodePSKeyValueCoding( (PSCoding.KeyValueCoding )anObject );
            } else {
                replacement = replacementObject( anObject );
                if (replacement != null && replacement != anObject) {
                    _encodeObject( replacement );
                } else {
                    //buffer().append( anObject.toString() );
                    throw new PlistException("Got an unsupported object of class: " +
                            anObject.getClass().getName() );
               }
            }
        }
        
        public void recycleAllInstances() {
            for ( int i=0; i < children().size(); i++ ) {
                Writer aChild = (Writer )children().elementAt(i);
                aChild.recycleAllInstances();
            }
            pool().recycleInstance( this );
        }
        
        public String toString() {
            return  buffer().toString();
        }
        
        protected void insertString( int insertionPoint, String str ) {
            _buffer.insert( offset() + insertionPoint, str );
            setOffset( offset() + str.length() );
        }
        
        protected void appendToParent() {
            String longMsg = null;
            String shortMsg = null;
            
            collectFromChildren();
            if (_parent != null) {
                try {
                    shortMsg = "Inserting at=[" + insertionPoint() + "]='" + buffer() +
                        "' parent["+  parent().getClass().getName() + "]" +
                        " bufLen=[" + parent().buffer().length() + "]";
                    longMsg = "Inserting at=[" + insertionPoint() + "]='" + buffer() +
                        "' parent["+  parent().getClass().getName() + "]='" + parent().buffer() +"'" +
                        " bufLen=[" + parent().buffer().length() + "]";
                     logger().verbose( this, longMsg );
                    _parent.insertString( insertionPoint(), _buffer.toString() );
                } catch (StringIndexOutOfBoundsException e) {                    
                    logger().error( this, longMsg, e );
                    throw new PlistException( longMsg );
                }
            }
        }
        
        protected void collectFromChildren() {            
            for ( int i=0; i < this.children().size(); i++ ) {
                Writer aChild = (Writer )this.children().get(i);
                aChild.appendToParent();
            }
        }
        
        public static class PSKeyValueCodingWriter extends Writer {
            
            public PSKeyValueCodingWriter() { }
            
            public PSKeyValueCodingWriter( Writer parent,
                    PSCoding.KeyValueCoding subject ) {
                
                super(parent);
                setTarget( subject );
                encodePSKeyValueCoding( subject() );
            }
            
            protected PSCoding.KeyValueCoding subject() {
                return (PSCoding.KeyValueCoding )target();
            }
            
            protected String subjectClass() { 
                return subject().getClass().getName(); 
            }
            
            protected JCEntity content() { return (JCEntity )subject(); }
            
            public void encodePSKeyValueCoding() {
                buffer().append(PSOBJECT_START);
                buffer().append( subject().classForCoder().getName() );
                appendConditionalSpace();
                
                if ( subject() instanceof JCEntity ) {
                    logger().trace( this, "Encoding content: " +
                            subjectClass() +
                            " name=" + content().getName() +
                            " ID=" + content().getGID() );
                    content().prepareForCoding();
                } 
                
//                parent().encodeDict( (Map )subject().properties() );
                encodeDict( (Map )subject().properties() );
                appendConditionalSpace();
                buffer().append(PSOBJECT_END);
            }
            
            protected void appendToParent() {
                if (shouldIndent()) {
                    int index = buffer().indexOf(PSOBJECT_END);
                    
                    buffer().insert( index, "\n" );
                    for (int i=0; i < parent().indentationLength(); i++) {
                        buffer().insert( index+1, " " );
                    }
                }
                super.appendToParent();
            }
            
        }  /// End PSKeyValueCodingWriter
        
        public abstract static class CollectionWriter extends Writer {
            
            public CollectionWriter() { }
            
            public CollectionWriter( Writer parent ) {
                super( parent );
            }
            
            protected void appendToParent() {
                if ( shouldIndent() ) {
                    int index = collectionEndIndex();
                    
                    buffer().insert( index, "\n" );
                    for ( int i=0; i < parent().indentationLength(); i++ ) {
                        buffer().insert( index+1, " " );
                    }
                }
                super.appendToParent();
            }
            
            protected abstract int collectionEndIndex();
            
        }   /// E N D   CollectionWriter
        
        /**
         * WARNING: the inst-vars are set to null when the instance is created
         * with Class.newInstance().
         */
        public static class ArrayWriter extends CollectionWriter {
            
            private static Stack _firstArrayEntryStack = new Stack();
            
            public ArrayWriter() {  }
            
            public ArrayWriter( Writer parent, Collection array ) {
                super(parent);
                setTarget( array );
                encodeArray( array() );
            }
            
            protected Stack stack() { return _firstArrayEntryStack; }
            protected void setStack( Stack aStack ) { _firstArrayEntryStack = aStack; }
            
            public void resetWriting() {
                super.resetWriting();
            }
            
            protected Collection array() { return (Collection )target(); }
            
            protected void setIsFirstArrayEntry(boolean bool) {
                if (bool)
                    _firstArrayEntryStack.push( Boolean.TRUE );
                else {
                    _firstArrayEntryStack.pop();
                    _firstArrayEntryStack.push( Boolean.FALSE );
                }
            }
            
            protected boolean isFirstArrayEntry() {
                boolean retVal = false;
                
                retVal = ((Boolean )_firstArrayEntryStack.peek() ).booleanValue();
                
                return retVal;
            }
            
            public Writer encodeArray() {
                Iterator iter = array().iterator();

                arrayStart();
                while( iter.hasNext() ) {
                    appendArrayEntry( iter.next() );
                }
                arrayEnd();
                
                return this;
            }
            
            protected void appendArrayEntry( Object obj ) {
                arraySeparator();
//                addChild( new ArrayEntryWriter(this, obj) );
                
                ArrayEntryWriter writer = (ArrayEntryWriter )pool().plistWriterOfClass(
                        ArrayEntryWriter.class, this, obj );
                addChild( writer );
                writer._encodeObject( obj );
            }
            
            protected void arrayStart() {
                setIsFirstArrayEntry( true );
                buffer().append(ARRAY_START);
                appendConditionalSpace();
            }
            
            protected void arrayEnd() {
                _firstArrayEntryStack.pop();
                appendConditionalSpace();
                buffer().append(ARRAY_END);
            }
            
            protected void arraySeparator() {
                if ( isFirstArrayEntry() == false ) {
                    buffer().append(ARRAY_SEPARATOR);
                    appendSpace();
                }
                setIsFirstArrayEntry(false);
            }
            
            protected int collectionEndIndex() {
                return buffer().indexOf( ARRAY_END );
            }
            
        } /// End ArrayWriter
        
        public static class ArrayEntryWriter extends Writer {
            
            public ArrayEntryWriter() { }
            
            public ArrayEntryWriter( Writer parent, Object entry ) {
                super(parent);
                setTarget( entry );
                _encodeObject( entry() );
            }
            
            protected Object entry() { return target(); }
            
            protected boolean needsIndentation() {
                return false;
            }

            protected void encodeArrayEntry() {
                _encodeObject( entry() );
            }
            
        }  /// End ArrayEntryWriter

        public static class DictWriter extends CollectionWriter {

            public DictWriter() { }

            public DictWriter( Writer parent, Map dict ) {
                super(parent);
                setTarget( dict );
            }

            protected Object dict() { return target(); }

            public DictWriter( Writer parent, Dictionary dict ) {
                super(parent);
                setTarget( dict );
            }

            protected int collectionEndIndex() {
                return buffer().indexOf( DICT_END );
            }

            public void encodeDict() {
                if ( dict() instanceof Map )
                    _encodeMap( (Map )dict() );
                else if ( dict() instanceof Dictionary )
                    _encodeDictionary( (Dictionary )dict() );
            }

            protected void _encodeMap( Map dict ) {
                Iterator iter = dict.keySet().iterator();
                dictStart();
                while ( iter.hasNext() ) {
                    Object aKey   = iter.next();
                    Object aValue = dict.get( aKey );
                    appendDictEntry( aKey, aValue );
                }
                dictEnd();
            }

            protected void _encodeDictionary( Dictionary dict ) {
                Enumeration iter = dict.keys();
                dictStart();
                while(iter.hasMoreElements()) {
                    Object aKey = iter.nextElement();
                    Object aValue = dict.get( aKey );
                    appendDictEntry( aKey, aValue );
                }
                dictEnd();
            }

            protected void appendDictEntry( Object aKey, Object aValue ) {
                DictEntryWriter writer = (DictEntryWriter )pool().plistDictEntryWriter(
                        this, aKey, aValue );
                addChild( writer );
                writer.encodeDictEntry();
                dictSeparator();
            }

            protected void dictStart() {
                buffer().append(DICT_START);
                appendConditionalSpace();
            }

            protected void dictEnd() {
                buffer().append(DICT_END);
            }

            protected void dictSeparator() {
                buffer().append(DICT_ENTRY_SEPARATOR);
                appendConditionalSpace();
            }

        } /// End DictWriter.
        
        public static class DictEntryWriter extends Writer {
            
            private Object _key;
            private Object _value;
            
            public DictEntryWriter() { }
            
            public DictEntryWriter( Writer parent, Object key, Object value ) {
                super(parent);
                setKey( key );
                setValue( value );
            }
            
            protected Object key() { return _key; }
            protected void setKey( Object k ) { _key = k; }
            protected Object value() { return _value; }
            protected void setValue( Object v ) { _value = v; }
            
            protected void encodeDictEntry() {
                this._encodeObject( key() );
                appendConditionalSpace();
                buffer().append( KEY_VALUE_SEPARATOR );
                appendConditionalSpace();
                this._encodeObject( this.replacementObject( value() ) );
//                this._encodeObject( value() );
            }
            
            protected boolean needsIndentation() {
                return false;
            }
            
        }  /// End DictEntryWriter
                
    }
    
    public static class Reader extends PSCustomCoder.Replacer
            implements PSCoding.Reading {
        
        private     String          _plistString;
        protected   Stack           _stack;
        private     Object          _returnValue;
        private     StringTokenizer _plistTokenizer;
        private     Class           _stackDictClass;
        private     PSDict          _processingMap = null;
        protected   Object          _token;
        
        private boolean _debug = false;
                
        public Reader() {
            this( null );
        }
        
        public Reader( String plistString ) {
            _plistString = plistString;
            _stack = new Stack();
            _stackDictClass = PSDict.class;
        }
        
        protected Object token() { return _token; }
        protected void setToken( Object token ) {
            _token = token;
        }
        
        protected PSDict processingMap() {
            if ( _processingMap == null ) {
                _processingMap = new PSDict(20);
                _processingMap.put( ESCAPE              , new EscapeChar()         );
                _processingMap.put( STRING_START        , new StringStart()        );
                _processingMap.put( INTEGER_START       , new IntegerStart()       );
                _processingMap.put( FLOAT_START         , new FloatStart()         );
                _processingMap.put( BOOLEAN_START       , new BooleanStart()       );
                _processingMap.put( PSOBJECT_START      , new PSObjectStart()      );
                _processingMap.put( DICT_START          , new DictStart()          );
                _processingMap.put( KEY_VALUE_SEPARATOR , new KeyValueSeparator()  );
                _processingMap.put( ARRAY_START         , new ArrayStart()         );
                _processingMap.put( DICT_ENTRY_SEPARATOR, new DictSeparator()      );
                _processingMap.put( ARRAY_SEPARATOR     , new ArraySeparator()     );
                _processingMap.put( PSOBJECT_END        , new PSObjectEnd()        );
                _processingMap.put( DICT_END            , new DictEnd()            );
                _processingMap.put( ARRAY_END           , new ArrayEnd()           );
            }
            
            return _processingMap;
        }

        public void resetReading() {
            _plistString = null;
            _stack.clear();
            _stackDictClass = PSDict.class;
        }
        
        public void setPlistString( String aPlist ) {
            _plistString = aPlist;
        }

        /**
         * Because only TreeMap garantees the order of dict entry
         * this method allows the caller to specify the dictionary class to use while
         * decoding a dict plist.
         * The class should be set to TreeMap.class when the caller expects the dict
         * entries to appear in a specific order.
         */
        public Class stackDictClass() { return _stackDictClass; }
        public void setStackDictClass( Class dictClass ) {
            _stackDictClass = dictClass;
        }
        
        public Object returnValue() { return _returnValue; }
        public void setReturnValue( Object value ) { _returnValue = value; }
        
        protected Map createDict() {
            try {
                return (Map )_stackDictClass.newInstance();
            } catch (Exception e) {
                throw new PlistException( "Invalid Dictionary class" );
            }
        }
        
        protected Object nextToken() {
            Object nToken = _plistTokenizer.nextToken();
            
            return nToken;
        }
        
        protected boolean hasMoreTokens() {
            return _plistTokenizer.hasMoreTokens();
        }
        
        public Object decodeObject() {             
            _plistTokenizer = new StringTokenizer( _plistString, DELIMITERS +
                    SKIP_CHARS, DELIM_ARE_TOKEN );
            while ( _plistTokenizer.hasMoreTokens() ) {
                _token = nextToken();
                if ( _token.equals(SPACE) ) continue;
                
//                if ( ( (String )_token ).length() > 3 )
//                    logger().verbose( this, "Token=[" + _token + "]" );
                
                TokProc tokenProcessor = (TokProc )processingMap().get( _token.toString() );
                
                if ( tokenProcessor != null ) {
                    tokenProcessor.processToken();
                } else {
                    commonString();
                }
            }                        
            Object original = originalObject( returnValue() );
            
            return original;
        }               
        
        protected String stackAsString() {
            if (!logger().isVerbosing())
                return "";
            return _stack.toString();
        }
        
        protected void escapeChar() {
            if ( _plistTokenizer.hasMoreElements() ) {
                _token = nextToken();
                commonString();
            } else
                logger().error( this, "Escape without next token" );
        }
        
        ////  S C A L A R   T Y P E S   //////////////////
        protected void booleanStart() {
            Boolean bool = null;
            
            _token = nextToken();
            bool = ( 
                       _token.equals( Y )    || _token.equals( y )     || 
                       _token.equals( YES )  || _token.equals( yes )   ||
                       _token.equals( TRUE ) || _token.equals( true_ ) 
                   ) ? Boolean.TRUE : Boolean.FALSE;
            _stack.push( bool );
        }
                
        protected void integerStart() {
            Object number = null;
            
            _token = nextToken();
            if ( _token.equals( INTEGER_START ) ) // if the token is repeated, use Long
                number = new Long( nextToken().toString() );
            else if ( _token.equals( BOOLEAN_START ) ) // If the token is followed by the boolean sign, use PSInt
                number = createInt( nextToken() );
            else {
                number = createInt( _token );
            }
            _stack.push( number );
        }
        
        protected PSInt createInt( Object token ) {
            return PSInt.parsePSInt( (String )token );
        }
        
        protected void floatStart() {
            Object number = null;
            
            _token = nextToken();
            if ( _token.equals( FLOAT_START ) )// if the token is repeated, use Double
                number = new Double( nextToken().toString() );
            else
                number = new Float( _token.toString() );
            _stack.push( number );
        }
        
        protected void stringStart() {
            PSBuffer buffer = pool().bufferInstance();
            
            while( hasMoreTokens() ) {
                _token = nextToken();
                if ( _token.equals( STRING_END ) ) break;
                if ( _token.equals( ESCAPE ) ) {
                    if ( hasMoreTokens() ) {
                        buffer.append( nextToken() );
                        continue;
                    } else
                        logger().error( this, "Escape without next token" );
                }
                buffer.append( _token );
            }
            Object stackElement = _stack.peek();
            if ( isCollection( stackElement) ) {
                ((Collection )stackElement ).add( buffer.toString() );
            } else {
                _stack.push( buffer.toString() );
            }
            pool().recycleInstance( buffer );
        }
        
        ////  P S O B J E C T   /////////////////////////
        protected void psObjectStart() {
            Object classToken = null;
            Class psObjectClass;
            PSCoding.KeyValueCoding psObject;
            PSCoding.CustomCoding customCoder;
            
            try {
                // TODO: Think about a way to maintain a mapping token => className
                // This may prevent calling nextToken().toString().
                // The mapping might be created by Writer and exploited here.
                classToken = nextToken();
                customCoder = coderForClassName( classToken );
                if ( customCoder != null ) {
                    psObject = (PSCoding.KeyValueCoding )customCoder;
                } else {                    
                    psObject = instanceForClassName( classToken );
                }
                if (psObject == null)
                    throw new PlistException("Couldn't instantiate object of class: " + classToken );
                _stack.push(psObject);
            } catch (ClassNotFoundException e) {
                throw new PlistException("Found unknown class name: " + classToken );
            }  catch (IllegalAccessException e) {
                throw new PlistException("Illegal Access to class: " + classToken );
            } catch (InstantiationException e) {
                throw new PlistException("Couldn't instantiate object of class: " + classToken );
            } catch (ConcurrentModificationException e) {
                throw new PlistException("Concurrent Modification while updating stack for token: " + classToken );
            }
        }

        protected PSCoding.KeyValueCoding instanceForClassName( Object classToken ) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
            PSCoding.KeyValueCoding psObject;
            Class psObjectClass;
            
            psObjectClass = Class.forName( classToken.toString() );
            psObject = (PSCoding.KeyValueCoding )psObjectClass.newInstance();

            return psObject;
        }

        protected void psObjectEnd() {
            Object stackElement, object;
            
            if (_stack.isEmpty())
                throw new PlistException("Found object end while stack is empty!");
            
            object = _stack.pop();
            if (_stack.isEmpty()) {
                setReturnValue( originalObject(object) );
                return;
            }
            stackElement = _stack.peek();
            if ( isCollection( stackElement ) ) {
                ((Collection )stackElement).add( originalObject(object) );
            } else
                _stack.push( originalObject(object) );
            
            String className = stackElement.getClass().getName();
            logger().verbose( this, "Finished building object: "+ className );
        }
        
        ////  A R R A Y   ///////////////////////////////
        protected void arrayStart() {
            _stack.push( new PSArray() );
        }
        
        protected void arrayEnd() {
            Object stackElement, array;
            
            if (_stack.isEmpty())
                throw new PlistException("Found array end while stack is empty!");
            
            array = _stack.pop();
            if (_stack.isEmpty()) {
                setReturnValue( array );
                return;
            }
            stackElement = _stack.peek();
            if ( isCollection( stackElement ) ) {
                ((Collection )stackElement).add( originalObjects( (Collection )array ) );
            } else
                _stack.push( originalObjects( (Collection )array ) );
        }
        
        protected void arraySeparator() { }
        
        //////////   D I C T    /////////////////////////
        protected void dictStart() {
            _stack.push( createDict() );
        }
        
        protected void dictEnd() {
            Object stackElement, dict;
            PSDict aDict;
            
            if (_stack.isEmpty())
                throw new PlistException("Found dict end while stack is empty!");
            
            Object obj = _stack.peek();            
            dict = _stack.pop();
            if (_stack.isEmpty()) {
                setReturnValue( dict );
                return;
            }
            stackElement = _stack.peek();
            if ( isCollection( stackElement ) ) {
                ( (Collection )stackElement ).add( dict );
            } else if ( isKeyValueCoding( stackElement ) ) {
                if ( dict instanceof PSDict )
                    aDict = (PSDict )dict;
                else
                    aDict = new PSDict( (Map )dict ); //TODO: Do not create new Dict
                
                if ( stackElement instanceof JCEntity ) {
                    registerContent( (JCEntity )stackElement, aDict );
                } else {
                    ( (PSCoding.KeyValueCoding )stackElement ).takePropertyDict( aDict );
                }
            } else
                _stack.push( dict );
        }
        
        protected void registerContent( JCEntity aContent, PSDict properties ) {
            aContent.takePropertyDict( properties );                       
        }
        
        protected void dictSeparator() {
            Object key, value;
            
            try {
                value = _stack.pop();
                key = _stack.pop();
            } catch (EmptyStackException e) {
                throw new PlistException("Dict entry with null key or value");
            }
            if (isDictMode()) {
                consumeKeyValuePair( key, value );
            } else
                throw new PlistException("Dict separator while not in dict mode");
        }
        
        protected void consumeKeyValuePair( Object key, Object value ) {
            Map map = null;            
            String mapClassName;
            String msg;

            try {
                value = originalObject(value);
                if (key != null && value != null) {
                    map = (Map )_stack.peek();
                    map.put( key, value );
                    
                    mapClassName = (map == null) ? null : map.getClass().getName();
//                    msg = "Consuming: " + "Key=["+key +
//                            "] Value="+value.getClass().getName() + " containerClass=" + mapClassName;
//                     logger().trace( this, msg );
                }
            } catch (Throwable e) {
                mapClassName = (map == null) ? null : map.getClass().getName();
                msg = "Error consuming K/V pair: " + "Key=["+key +
                        "] Value="+value + " containerClass=" + mapClassName;
                logger().error( this, msg );
                throw new PlistException( msg );
            }
        }
        
        protected void keyValueSeparator() {
            if (_stack.size() < 2)
                throw new PlistException("Key/value separator with no key/value pair");
        }
        
        protected Object currentContainer() {
            Object container = null;
            try {
                container = _stack.peek();
            } catch (EmptyStackException e) {}
            
            return container;
        }
        
        protected boolean isArrayMode() {
            boolean mode = ( isCollection( currentContainer() ) );
            
            return mode;
        }
        
        protected boolean isDictMode() {
            return !isArrayMode();
        }
        
        protected boolean shouldSkipToken( Object token ) {
            return ( SKIP_CHARS.indexOf( (String )token ) >= 0 );
        }
        
        protected void commonString() {
            if ( shouldSkipToken( _token ) ) return;
            
            if ( isDictMode() ) {
                String str = _token.toString();                
                _stack.push( str );
            } else if ( isArrayMode() ) {
                Collection array = (Collection )currentContainer();
                if (array != null)
                    array.add( _token.toString() );
            } else {
                throw new PlistException("Can't process token: " + "'" + _token + "'" );
            }
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
    
    public static class PlistException extends Error {
        
        public PlistException(String msg) {
            super(msg);
        }
        
    }
    
    public static class NullValue extends PSObject {
        
        public void takePropertyDict( PSDict dict ) { }
        
    }
    
    
} /// End PSPlistCoder

