

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

package com.camcars;

import java.text.DateFormat;
import java.util.*;

public class PSLogger {
    
    private boolean _debug = true;
    private boolean _trace = false;
    private boolean _verbose = false;
    private boolean _mute = false;
    private boolean _stackTracing = false;
    
    private boolean _savedDebugValue   = _debug;
    private boolean _savedTraceValue   = _trace;
    private boolean _savedVerboseValue = _verbose;
    
    private Stack _savedDebugStack   = null;
    private Stack _savedTraceStack   = null;
    private Stack _savedVerboseStack = null;
    private Stack _savedMuteStack    = null;
    
    private static PSBuffer _Buffer = null;
    private static DateFormat _DateFormat = null;

    public PSLogger() {}

    public PSLogger( boolean debug, boolean trace, boolean verbose, boolean mute ) {
        setDebug( debug );
        setTrace( trace );
        setVerbose( verbose );
        setMute( mute );
    }
    
    //////////////////////////   A C C E S S O R S   ///////////////////////////
    private static PSBuffer buffer() {
        if ( _Buffer == null ) {
            _Buffer = new PSBuffer(); 
        } else {
            _Buffer.clearContent();
        }
        
        return _Buffer;
    }
    
    public PSLogger setDebug( boolean debug ) {
        _debug = debug;
        return this;
    }

    public PSLogger setMute( boolean mute ) {
        _mute = mute;
        return this;
    }

    public PSLogger setTrace( boolean trace ) {
        _trace = trace;
        return this;
    }

    public PSLogger setVerbose( boolean verbose ) {
        _verbose = verbose;
        return this;
    }
    
    public PSLogger setStackTracing(boolean stackTracing) {
        this._stackTracing = stackTracing;
        return this;
    }

    public boolean isLogging()   { return !_mute; }

    public boolean isDebugging() { return isLogging()   && _debug; }

    public boolean isTracing()   { return isDebugging() && _trace; }

    public boolean isVerbosing() { return isTracing()   && _verbose; }
    
    /**
     * The boolean value returned by this method controls if the logger prints
     * the exception stack traces or not.
     */
    public boolean isStackTracing() {
        return _stackTracing || isTracing();
    }
    
     public String toString() {
        return "[PSLogger" +  " Debug="         + _debug +
                              " Trace="         + _trace +
                              " Verbose="       + _verbose +
                              " StackTracing="  + _stackTracing +
                              " Mute="          + _mute + "]";
    }

    ////////////////////////   L O G G I N G   /////////////////////////////
    
     public DateFormat dateFormat() {
         if ( _DateFormat == null ) {
             _DateFormat = DateFormat.getTimeInstance();
         }
         
         return _DateFormat;
     }
     
     public String timeStamp() {
         return dateFormat().format( new Date() );
     }
     
    public void log( String message ) {
        if ( ! _mute ) {
            if ( message.length() < 2 ) {
                System.out.println( timeStamp() + " " + message );
            }
            System.out.println( timeStamp() + " " + message );
        }
    }
    
    public void log( Object sender, String message ) {
            log( concat( sender, message ) );
    }

    public void warn( String message ) {
        log( message );
    }
    
    public void warn( Object sender, String message ) {
        warn( concat( sender, message ) );
    }

    public void separator() {
        // The Bus uses this  blank string for some purpose. But prevents the logs to be readable...
//        System.out.println( " " );
    }

    // We print to System.err as a mean to highlight the msg in Jbuilder
    public void shout( String message ) {
        error( message );
    }
    
    public void shout( Object sender, String message ) {
        shout( concat( sender, message ) );
    }

    public void debug( String message ) {
        if ( _debug )
            log( message );
    }
    
    public void debug( Object sender, String message ) {
        if ( _debug )
            log( concat( sender, message ) );
    }
    
    private String concat( Object sender, String message ) {
        PSBuffer buffer = buffer();
        
        buffer.append( sender.getClass().getName() );
        buffer.append(": ").append( message );
        
        return buffer.toString();
    }
    
    public void trace( String message ) {
        if ( _trace )
            log( message );
    }
    
    public void trace( Object sender, String message ) {
        if ( _trace )
            log( concat( sender, message ) );
    }


    public void verbose( String message ) {
        if ( _verbose )
            log( message );
    }
    
    public void verbose( Object sender, String message ) {
        if ( _verbose )
            log( concat( sender, message ) );
    }

    public void error( String message ) {
//        if ( isLogging() )
            System.err.println( timeStamp() + " " + message );
    }

    public void error( String message, Throwable e ) {
        error( message + " ::: " + e.toString() );
        if ( isStackTracing() ) {
            e.printStackTrace();
        }
    }
    
    public void error( Object sender, String message ) {
        error( concat( sender, message ) + " ::: " );
    }
    
    public void error( Object sender, String message, Throwable e ) {
        error( concat( sender, message ) + " ::: ", e );
    }
    
    ///////////////////   B E H A V I O U R  A L T E R A T I O N   ///////////

    public PSLogger alterDebug( boolean bool ) {
        alterMute( false );
        setSavedDebugValue(_debug);        
        return setDebug( bool );
    }
    
    public PSLogger restoreDebug() { 
        restoreMute();
        if ( savedDebugStack().isEmpty() ) return this;
        return setDebug( savedDebugValue() ); 
    }
    
    public PSLogger alterTrace( boolean bool ) {
        alterDebug( bool );
        setSavedTraceValue(_trace);        
        return setTrace( bool );
    }
    
    public PSLogger restoreTrace() { 
        restoreDebug();
        if ( savedTraceStack().isEmpty() ) return this;
        return setTrace( savedTraceValue() ); 
    }
    
    public PSLogger alterVerbose( boolean bool ) {
        alterTrace( bool );
        setSavedVerboseValue(_verbose);        
        setVerbose( bool );
        return this;
    }
    
    public PSLogger restoreVerbose() { 
        restoreTrace();
        if ( savedVerboseStack().isEmpty() ) return this;
        setVerbose( savedVerboseValue() );
        return this;
    }
    
    public PSLogger alterMute( boolean bool ) {
        setSavedMuteValue(_mute);        
        return setMute( bool );
    }
    
    public PSLogger restoreMute() { 
        if ( savedMuteStack().isEmpty() ) return this;
        return setMute( savedMuteValue() ); 
    }
    
    private boolean _savedStackValue( Stack stack ) { 
        Boolean retVal;
        
        try {
            retVal = (Boolean )stack.pop();
        } catch (EmptyStackException e) {
            error( this, "SavedValueStack is Empty" );
            retVal = new Boolean(false);
        }
        return retVal.booleanValue();
    }
    
    public boolean savedDebugValue() { 
        return _savedStackValue( savedDebugStack() );
    }
    
    public boolean savedTraceValue() { 
        return _savedStackValue( savedTraceStack() );
    }
    
    public boolean savedVerboseValue() { 
        return _savedStackValue( savedVerboseStack() ); 
    }
    
    public boolean savedMuteValue() { 
        return _savedStackValue( savedMuteStack() ); 
    }

    public void setSavedDebugValue(boolean savedValue) {
        savedDebugStack().push( new Boolean(savedValue) );
    }

    public void setSavedTraceValue(boolean savedValue) {
        savedTraceStack().push( new Boolean(savedValue) );
    }

    public void setSavedVerboseValue(boolean savedValue) {
        savedVerboseStack().push( new Boolean(savedValue) );
    }
    
    public void setSavedMuteValue(boolean savedValue) {
        savedMuteStack().push( new Boolean(savedValue) );
    }

    public Stack savedDebugStack() {
        if (_savedDebugStack == null) {
            _savedDebugStack = new Stack();
        }
        return _savedDebugStack;
    }

    public Stack savedTraceStack() {
        if (_savedTraceStack == null) {
            _savedTraceStack = new Stack();
        }
        return _savedTraceStack;
    }

    public Stack savedVerboseStack() {
        if (_savedVerboseStack == null) {
            _savedVerboseStack = new Stack();
        }
        return _savedVerboseStack;
    }
    
    public Stack savedMuteStack() {
        if (_savedMuteStack == null) {
            _savedMuteStack = new Stack();
        }
        return _savedMuteStack;
    }

}