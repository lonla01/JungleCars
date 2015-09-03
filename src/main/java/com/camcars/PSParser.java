package com.camcars;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
//import java.util.StringTokenizer;
import java.util.TreeMap;
/**
 *
 * PSBibleParser.java
 *
 * Created on 10 juin 2009, 13:28
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */


public abstract class PSParser extends PSObject implements PSCoding.Parsing {
    
    private PSCoding.Writing _writer;
    private PSCoding.Reading _reader;
    protected File _filePath;
    private PSTokenizer _tokenizer = null;
    private PSCoding.Parsing _textLoader;
    private Class _textLoaderClass = null;
    private JCEntity _metaContent = null;
    private String _bufferedText = null;
    private BufferedReader _bufferedReader = null;    
    public String _textEncoding = null;

    public static final boolean DELIM_ARE_TOKEN = true;
    public static final String FIELD_SEPARATOR = "///";
    public static final String LINE_SEPARATOR = "\n";
    public static final String DEFAULT_TEXT_ENCODING = "UTF-16";
    
    public PSParser() { this( null ); }
    
    public PSParser( File filePath ) {
        setTextLoaderClass( PSParser.TextParser.class );
        if ( filePath != null )
            setFilePath( filePath );
    }
    
    protected  PSCoding.Writing _writer() { return _writer; }
    protected void setWriter( PSCoding.Writing writer) {  this._writer = writer; }
    
    protected PSCoding.Reading _reader() { return _reader; }
    protected void setReader(PSCoding.Reading reader) { this._reader = reader; }
    
    public URL url() {
        URL url = null;
        try {
            //URL url = getClass().getResource( "Test((File )path).getName() );
            url = filePath().toURL();
        } catch (MalformedURLException ex) {
            logger().error( this, "Got a bad URL: " + filePath().getAbsolutePath(), ex );
            url = null;
        }
        
        return url;
    }
    
    public JCEntity metaContent() { return _metaContent; }
    
    public void setMetaContent( JCEntity aVersion ) {
        _metaContent = aVersion;
    }
    
    public void resetCoding() {
        setFilePath( null );
        setTokenizer( null );
    }
    
    public File filePath() { return _filePath; }
    public void setFilePath( File filePath ) {
        if ( filePath == null ) return;
        _filePath = filePath.getAbsoluteFile();
        if ( _textLoader() != null )
            _textLoader().setFilePath( filePath );
    }
    
    protected PSCoding.Parsing textLoader() {
        if ( _textLoader == null ) {
            try {
                _textLoader = (PSCoding.Parsing )textLoaderClass().newInstance();
                _textLoader().setFilePath( filePath() );
            } catch ( Exception e ) {
                error( this, "Cannot create text parser", e );
                _textLoader = null;
            }
        }
        
        return _textLoader;
    }
    
    public TextParser _textLoader() { return (TextParser )textLoader(); }
    protected Class textLoaderClass() { return _textLoaderClass; }
    protected void setTextLoaderClass( Class aClass ) {
        _textLoaderClass = aClass;
    }

    protected BufferedReader bufferedReader() {
        if ( _bufferedReader == null ) {
            _bufferedReader = createBufferedReader();
        }

        return _bufferedReader;
    }

    protected void setBufferedReader( BufferedReader aReader ) {
        _bufferedReader = aReader;
    }

    protected BufferedReader createBufferedReader() {
        BufferedReader reader;

        try {
            logger().debug( this, "Creating buffered reader with " + textEncoding() + " encoding." );
            InputStream is = url().openStream();
            InputStreamReader isr = new InputStreamReader( is, textEncoding() );
            reader = new BufferedReader( isr );
        } catch (Exception e) {
            logger().error(this, "Exception while creating BufferedReader", e);
            reader = null;
        }

        return reader;
    }

    protected void setTextLoader( PSCoding.Parsing loader ) {
        logger().debug(this, "Setting Text Loader");
        _textLoader = loader;
        if (_textLoader() != null) {
            _textLoader().setFilePath( filePath() );
        }
    }

    public String textEncoding() {
        if ( _textEncoding == null ) {
            _textEncoding = DEFAULT_TEXT_ENCODING;
        }
        
        return _textEncoding;
    }

    public void setTextEncoding( String encoding ) {
        _textEncoding = encoding;
        _textLoader().setTextEncoding( encoding );
    }

    protected String bufferedText() {
        if ( _bufferedText == null ) {
            _bufferedText = (String )textLoader().decodeObject();
        }

        return _bufferedText;
    }

    protected PSTokenizer tokenizer() { return _tokenizer; }
    protected void setTokenizer( PSTokenizer tok ) {
        _tokenizer = tok;
    }
    // This is a helper for the PSWatcher.WatchOwner interface.
    public PSTokenizer _tokenizer() { return tokenizer(); }
    public boolean hasMoreTokens() { return _tokenizer.hasMoreTokens(); }
    public PSString nextToken() { return _tokenizer.nextToken(); }
    
    ///   E N C O D I N G   ////////////////////////////////////////////////
    public void encodeObject( Object anObject ) {
        assertFalse( "Cannot encode null object",  anObject == null );
        logger().trace( this, "Encoding: " + anObject.getClass().getName() );
        initForWriting( filePath() );
        _writer().encodeObject( anObject );
        logger().verbose( this, "Finished encoding: " );
    }
    
    public Object decodeObject() {
        initForReading( filePath() );
        return _reader().decodeObject();
    }
    
    public abstract void initForWriting( Object data );
    
    public abstract void initForReading( Object data );    
    
    ///   N E S T E D   C L A S S E S   /////////////////////////////////////
    public static class ReadWrite extends PSCustomCoder.Replacer {
        
        private PSParser _parser;

        public ReadWrite() {}

        public ReadWrite( PSParser codec ) {
            setParser( codec );
        }

       protected PSParser parser() { return _parser; }
        protected void setParser( PSParser parser ) { _parser = parser; }
        
        protected File filePath() { return parser().filePath(); }
        protected TextParser _textLoader() {
            return parser()._textLoader();
        }
        
        protected BufferedReader bufferedReader() {
            return parser().bufferedReader();
        }
        
        protected String bufferedText() {
            return parser().bufferedText();
        }
        
        protected PSTokenizer tokenizer() {
            return parser().tokenizer();
        }
        
        protected void setTokenizer( PSTokenizer tok ) {
            parser().setTokenizer( tok );
        }
        
    }  // EndPSParser.ReadWrite
    
    /**
     * Was StreamCoder.
     */
    public static class TextParser extends PSParser implements PSCoding.Parsing {
        
        private Object _outputStream;
        private Object _inputStream;
        
        private Boolean _hasByteMark = null;
        
//        protected StringBuffer _textBuffer;
        private boolean _writeUTFEncoding = true;
        
        public TextParser() {
            this( null );
        }
        
        public TextParser( File filePath ) {
            this( filePath, true );
        }
        
        public TextParser(File filePath, boolean writeUTFEncoding) {
            super();
            setFilePath( filePath );
            _writeUTFEncoding = writeUTFEncoding;
        }
        
        //////////////////////   A C C E S S O R S   /////////////////////////
        // This method overrids the one in superclass to prevent and infinite loop.
        public void setTextEncoding( String encoding ) {
            _textEncoding = encoding;
        }

        // This method overrids the one in superclass to prevent and infinite loop.
        public void setFilePath( File filePath ) {
            _filePath = filePath;
        }
        
        protected BufferedInputStream inputStream() {
            return (BufferedInputStream )_inputStream();
        }
        protected Object _inputStream() { return _inputStream; }
        protected void setInputStream( Object aReader ) {
            _inputStream = aReader;
        }
        protected BufferedReader bufferedInputStream() {
            return (BufferedReader )_inputStream();
        }
        
        protected BufferedOutputStream outputStream() {
            return (BufferedOutputStream )_outputStream();
        }
        protected Object _outputStream() { return _outputStream; }
        protected void setOutputStream( Object aWriter ) { _outputStream = aWriter; }
        
        ///////////////////  E N C O D I N G    T E X T    ///////////////////
        
        public static String decodeString( File filePath ) {
            PSCoding.Parsing aCoder = new TextParser( filePath );
            
            return (String )aCoder.decodeObject();
        }
        
        /**
         * Reads the text from specified file, detecting UTF-16 encoding
         * Then breaks the text into String array, delimited at every line break
         */
                private String _decodeString( File fileName ) throws Exception {
//            PSBuffer textBuffer = pool().bufferInstance();
            StringBuffer textBuffer = new StringBuffer();
            String fileText = null;
            int numBytes = 0;

            numBytes = inputStream().available();
            if (numBytes == 0) {
                throw new Exception( "Text file " + fileName + " is empty");
            }
            byte byteData[] = new byte[ numBytes ];
            inputStream().read( byteData, 0, byteData.length );
            if ( byteData.length >= 2 && dataHasByteMark( byteData ) ) {
                // If byte mark is found,
                /// then use UTF-16 encoding to convert bytes...
                debug( this, "Reading using " +  textEncoding() + " encoding..." );
                setHasByteMark( Boolean.TRUE );
            } else {
//                debug( this, "Reading using system default encoding..." );
                setHasByteMark( Boolean.FALSE );
            }
            String bufferedText = bynaryToString( byteData, byteData.length );

//            pool().recycleInstance( textBuffer );

            return bufferedText;
        }

        private String ___decodeString( File fileName ) throws Exception {
//            PSBuffer textBuffer = pool().bufferInstance();
            StringBuffer textBuffer = new StringBuffer();
            String fileText = null;
            int numBytes = 0;

//            super.initForReading( filePath() );
            numBytes = inputStream().available();
            if (numBytes == 0) {
                throw new Exception( "Text file " + fileName + " is empty");
            }
            byte byteData[] = new byte[8192];
            int nch;
            while ( (nch = inputStream().read( byteData, 0, byteData.length ) ) != -1) {
                if ( _hasByteMark == null ) {
                    if ( nch >= 2 && dataHasByteMark( byteData ) ) {
                        /// If byte mark is found, 
                        /// then use UTF-16 encoding to convert bytes...   
                        debug( this, "Reading using " +  textEncoding() + " encoding..." );
                        setHasByteMark( Boolean.TRUE );
                    } else {                    
                        debug( this, "Reading using system default encoding..." );
                        setHasByteMark( Boolean.FALSE );
                    }
                }
                fileText = bynaryToString( byteData, nch );
                logger().debug( this, "Text iteration: charCount = " + nch );
                logger().debug( this, "Text iteration: text = " + fileText );
                textBuffer.append( fileText );
            }
            
            //logger().debug( "Char count from file: " + textBuffer.length() + " Available=" + numBytes );
            String bufferedText = textBuffer.toString();
            
//            pool().recycleInstance( textBuffer );
            
            return bufferedText;
        }
        
        private boolean dataHasByteMark( byte[] byteData ) {
            return  ( byteData[0] == (byte) 0xFF && byteData[1] == (byte) 0xFE ) ||
                    ( byteData[0] == (byte) 0xFE && byteData[1] == (byte) 0xFF );
            
        }
        
        private Boolean hasByteMark() {
            if ( _hasByteMark == null )
                _hasByteMark = Boolean.FALSE;
            
            return _hasByteMark;
        }
        
        private void setHasByteMark( Boolean bool ) {
            _hasByteMark = bool;
        }
        
        private String bynaryToString( byte[] byteData, int charCount )
        throws Exception {
            if ( hasByteMark().booleanValue() ) {                
                return new String( byteData, 0, charCount, textEncoding() );
            } else {                
                return new String( byteData, 0, charCount );
            }
        }
        
        public void encodeString( String aString ) {
            try {
                initForWriting( filePath() );
                _encodeString( aString );
            } catch (Exception e) {
                logger().error( "Exception while writing object", e );
            } finally {
                _finalizeWriter();
            }
        }
        
        protected void _encodeString( String inputText ) throws Exception {
            byte toBeWritten[];
            
            if (_writeUTFEncoding) {
                debug( this, "Writing using UTF-16 encoding" );
                toBeWritten = inputText.getBytes( "UTF-16" );
            } else {
                debug( this, "Writing using system default encoding" );
                toBeWritten = inputText.getBytes();
            }
            outputStream().write( toBeWritten, 0, toBeWritten.length );
        }
        
        ////////////////////   E N C O D I N G   O B J E C T S   /////////////////
        
        public Object decodeObject() {
            assertTrue("Could not find coder file: " + filePath().getAbsolutePath(),
                    filePath().exists() );
            try {
                initForReading( filePath() );
                return _decodeString( filePath() );
            } catch (Exception e) {
                logger().error( "Exception while reading object", e );
            } finally {
                _finalizeReader();
            }
            
            return null;
        }
        
        public void initForReading(Object path) {
            try {
                FileInputStream fin = new FileInputStream( (File )path );
                setInputStream( new BufferedInputStream(fin) );
            } catch (Exception e) {
                logger().error( "Exception while preparing coder for reading", e );
            }
        }
        
        private void _finalizeReader() {
            try {
                if (inputStream() != null)
                    inputStream().close();
            } catch ( Exception ex ) {
                logger().error( "Failed to Close File", ex );
            }
        }
        
        public void encodeObject( Object anObject ) {
            if ( anObject instanceof String )
                encodeString( (String )anObject );
            else
                logger().error( this, "Don't know how to encode object: " + anObject );
        }
        
        public void initForWriting( Object path ) {
            try {
                FileOutputStream fstrm = new FileOutputStream( (File )path);
                //_writer = new ObjectOutputStream(fstrm);
                setOutputStream( new BufferedOutputStream(fstrm) );
            } catch (Exception e) {
                logger().error( this, "Exception while preparing coder for writing", e );
            }
        }
        
        private void _finalizeWriter() {
            try {
                if (outputStream() != null)
                    outputStream().close();
            } catch ( Exception ex ) {
                logger().error( "Failed to Close File", ex );
            }
        }
                 
    } // End TextParser

    
} // End PSParser



