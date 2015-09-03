
package com.camcars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;
/**
 * PSGlobalID.java
 *
 * Created on 24 juin 2009, 17:30
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

public class PSID extends PSRoot {
    
    private Object _pKey;
    
    public PSID() {}
    
    public PSID( Object pKey ) { setPKey( pKey ); }
    
    public Object pKey() { return _pKey; }

    // PSRef subclass of PSID seem to use a String as primary key. But we'll like to
    // to enforce the usage of PSInt as a primary key for all other PSIDs.
    public void setPKey( Object pKey ) {
        if ( pKey.getClass() == String.class ) {
            pKey = PSInt.with( pKey );
        }
        _pKey = pKey;
    }
    
//    public Object pKey() { return valueForKey( "pK" ); }    
//    public void setPKey( Object pKey ) { takeValueForKey( pKey, "pK" ); }
    
    ///   U T I L I T I E S   //////////////////////////////////////////////////
    public String toString() { 
        return getClass().getName() + "{ pKey=["+_pKey+"] }";
    }
    
    public int hashCode() {
//        String toString = _pKey.toString();
//        String hashString = String.valueOf( _pKey.hashCode() );
//
//        if ( ! toString.equals( hashString ) ) {
//            debug( this, toString + " hash=" + hashString );
//            debug( this, "Stop" );
//        }
//
//        toString = _pKey.toString();
//        hashString = String.valueOf( _pKey.hashCode() );

        return _pKey.hashCode();
    }
    
    public boolean equals(Object other) {
        if (other == null) return false;
        if ( other.getClass().equals( this.getClass() ) ) {
            PSID another = (PSID )other;
            boolean value =  _pKey.equals( another.pKey() );
            return value;
        }
        return false;
    }
    
    ///   P E R S I S T E N C E   //////////////////////////////////////////////
    public PSDict snapshot() {
        PSDict aDict = super.snapshot();
        
        aDict.put( "k", pKey() );
        
        return aDict;
    }
    
    public static Object objectFromSnapshot( PSDict aDict ) {
        PSID anID = new PSID();
        
        anID.takePropertyDict( aDict );
        
        return anID;
    }

    public void takePropertyDict( PSDict aDict ) {
        setPKey( PSInt.with( aDict.get( "k" ) ) );
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject( pKey() );
    }
     
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        setPKey( in.readObject() );
    }
    
    /// N E S T E D   C L A S S E S   //////////////////////////////////////////
    public static class Generator extends PSObject {
                
        private PSInt _startValue    = PSInt.with(1);
        private PSInt _batchSize     = PSInt.with(1000);
        private PSInt _currentPK     = null;
        private Stack _allocStack    = null;
        private PSDict _classToStack = null;
        
        private static Generator _defaultGenerator = null;
        private static boolean   _resetCounting    = false;
        
        public Generator() {
            setClassToPK( new PSDict(100) );
        }
        
        public PSInt batchSize() { return _batchSize; }
        public void setBatchSize( PSInt i ) { _batchSize = i; }
        
        public static Generator defaultGenerator() {
            // If reset, then allocate new generator
            if ( shouldResetCounting() ) {
                _defaultGenerator = new Generator();
                setResetCounting(false);
                return _defaultGenerator;
            }
            
            // If an instance is already in use, return it
            if ( _defaultGenerator != null ) return _defaultGenerator;
            
            // If not try unarchive an old instance
            try {
                _defaultGenerator = (Generator )classCoder().decodeObject();
            } catch (Throwable e ) {
                logger().error( "Generator: Error loading primary key file", e );
                _defaultGenerator = null;
            }
            if ( _defaultGenerator != null ) return _defaultGenerator;
            
            // If unarchive failed, create a fresh instance
            _defaultGenerator = new Generator();      
            
            return _defaultGenerator;
        }
        
        protected static PSCoding.Parsing classCoder() {
            return coderForClass( Generator.class );
        }
        
        public PSID nextIDForClass( Class aClass ) {
            try {
                PSInt currentPK = allocateForClass( aClass );
//                logger().trace( this, "Next PK=" + currentPK + " Class=" + aClass.getName() );
                
                return new PSID( currentPK ); 
            } catch ( Throwable e ) {
                logger().error( this, "Error generating primary key", e );
                return null;
            }            
        }
        
        protected PSInt allocateForClass( Class aClass ) {
            PSInt pk;
            
            if ( allocStackForClass( aClass ).isEmpty() ) {
                preAllocateForClass( aClass );
            }
            pk = (PSInt )allocStackForClass( aClass ).pop();
            
            return pk;
        }
        
        protected void preAllocateForClass( Class aClass ) {
            PSInt pk;
            PSInt maxPK, minPK;
            
            minPK = (PSInt )classToPK().get( aClass.getName() );
            if (minPK == null)
                minPK = startValue();
            
            maxPK = PSInt.with( minPK.val() + batchSize().val() - 1 );
            classToPK().put( aClass.getName(), maxPK.incr() );
            // Because the stack is LIFO, we fill it in reverse order.
            for ( pk = maxPK; pk.val() >= minPK.val(); pk = pk.decr() ) {                
                allocStackForClass( aClass ).add( pk );                
            }
            
            autoSave();
        }

        public void autoSave() {
            try {
//                logger().alterVerbose(false);
                super.autoSave();
            } finally {
//                logger().restoreVerbose();
            }
        }
        
        protected Stack allocStackForClass( Class aClass ) {
            Stack stack = (Stack )classToStack().get( aClass.getName() ); 
            if ( stack == null ) {
                stack = new Stack();
                classToStack().put( aClass.getName(), stack );
            }
            return stack;
        }
        
        protected PSDict classToStack() {
            if ( _classToStack == null ) {
                _classToStack = new PSDict();
            }
            return _classToStack;
        }
        
        public static boolean shouldResetCounting() {  return _resetCounting; }

        public static void setResetCounting(boolean resetCounting) {
            _resetCounting = resetCounting;
        }
        
        protected PSInt startValue() { return _startValue; }
        
        public PSDict classToPK() {
            return (PSDict )valueForKey( "classToPK" );
        }
        
        public void setClassToPK( PSDict aDict ) {
            takeValueForKey( aDict, "classToPK" );
        }
        
        public void takePropertyDict( PSDict aDict ) {
            setClassToPK( (PSDict )aDict.get( "classToPK" ) );
        }

    } /// End Provider
    
    public static class Coder extends PSCustomCoder {
        
        public Coder() { super( PSID.Coder.class ); }
        
        public Coder( PSID aGid ) {
            this();
            setCodingTarget( aGid );
            fillPropertyDict();
        }
        
        public void takePropertyDict( PSDict dict ) {
            super.takeValueForKey( dict.get( "k" ), "k" );
        }
        
        public void fillPropertyDict() {
            super.takeValueForKey( id().pKey(), "k" );
        }
        
        public PSID id() { return (PSID )codingTarget(); }
        
        public PSID decodeID() {
            return (PSID )decodeObject();
        }
        
        public Object decodeObject() {
            PSID aGid = new PSID();
            
            aGid.setPKey( super.valueForKey( "k" ) );
            
            return  aGid;
        }
        
    } // End Coder
    
} /// End PSGID
