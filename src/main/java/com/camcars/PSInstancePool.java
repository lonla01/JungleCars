
/**
 * PSInstancePool.java
 *
 * Created on 5 ao�t 2009, 22:53
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */

package com.camcars;

import java.util.Iterator;
import java.util.Stack;

public class PSInstancePool extends PSObject {
    
    private static PSInstancePool _defaultPool = null;
    private PSDict  _classToStack       = null;
    private int     _batchSize          = 5;
    
    private int     _allocCount         = 0;
    private int     _recycleCount       = 0;
    
    public static PSInstancePool defaultPool() {
        if ( _defaultPool == null ) {
            _defaultPool = new PSInstancePool();
        }
        return _defaultPool;
    }
    
    public void resetRecycling() {
        _defaultPool = null;
//        store().resetStats();
    }
    
    public StatStore store() { return StatStore.store(); }
    
    public String stackString() {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = classToStack().keySet().iterator();
        while ( iter.hasNext() ) {
            Class aClass = (Class )iter.next();
            Stack stack = (Stack )classToStack().get( aClass );
            if ( stack.size() > 3 ) {
                buffer.append( "\n");
                buffer.append( "ClassToStack " ).append( aClass.getName() );
                buffer.append( "[" ).append( stack.size() ).append( "]" );
            }
        }
        return buffer.toString();
    }
    
    ///   P U B L I C   I N T E R F A C E   ////////////////////////////////////
    public Object instanceOfClass( Class aClass ) {
        Stack stack;
        
//        _allocCount++;
//        StatStore.store().incrAllocsForClass( aClass );
        
        stack = stackForClass( aClass );
        if ( stack.isEmpty() )
            stack = reFillStackForClass( aClass );
        
        return stack.pop();
    }
    
    public void recycleInstance( Object anObject ) {
        Stack stack;
        
        if ( anObject == null ) return;
        stack = stackForClass( anObject.getClass() );
        
        stack.add( anObject );
        
//        _recycleCount++;
//        StatStore.store().incrRecyclesForClass( anObject.getClass() );
    }
    
    public PSPlistCoder.Writer plistWriterOfClass( Class aClass,
            PSPlistCoder.Writer parent, Object target ) {
        
        PSPlistCoder.Writer aWriter =
                (PSPlistCoder.Writer )instanceOfClass( aClass );
        aWriter.resetWriting();
        aWriter.setParent( parent );
        aWriter.setTarget( target );
        
        return aWriter;
    }
    
    public PSPlistCoder.Writer plistDictEntryWriter(
            PSPlistCoder.Writer parent, Object key, Object value ) {
        
        PSPlistCoder.Writer.DictEntryWriter aWriter =
                (PSPlistCoder.Writer.DictEntryWriter )instanceOfClass(
                PSPlistCoder.Writer.DictEntryWriter.class );
        aWriter.resetWriting();
        aWriter.setParent( parent );
        aWriter.setKey( key );
        aWriter.setValue( value );
        
        return aWriter;
    }
    
    public PSBuffer bufferInstance() {
        PSBuffer buffer;
        
        buffer = (PSBuffer )instanceOfClass( PSBuffer.class );
        buffer.clearContent();
        
        return buffer;
    }
    
    public PSBuffer bufferInstance( Object content ) {
        PSBuffer buffer;
        
        buffer = bufferInstance();
        buffer.append( content );
        
        return buffer;
    }
    
    public PSVisitor visitorOfClass( Class aClass ) {
        return (PSVisitor )instanceOfClass( aClass );
    }
    
    public PSArray.CollectVisitor collectVisitor() {
        PSVisitor aVisitor;
        
        aVisitor = (PSVisitor )visitorOfClass( PSArray.CollectVisitor.class );
        aVisitor.resetState();
        
        return (PSArray.CollectVisitor )aVisitor;
    }
    
    ///   P R I M I T I V E S    ///////////////////////////////////////////////
    protected PSDict classToStack() {
        if ( _classToStack == null ) {
            _classToStack = new PSDict();
        }
        return _classToStack;
    }
    
    protected Stack stackForClass( Class aClass ) {
        Stack stack;
        
        stack = (Stack )classToStack().get( aClass );
        if ( stack == null ) {
            stack = new Stack();
            classToStack().put( aClass, stack );
        }
        return stack;
    }
    
    protected Stack reFillStackForClass( Class aClass ) {
        Stack stack;
        
        try {
//            debug( this, "Refilling stack for class: " + aClass.getName() );
            stack = stackForClass( aClass );
            if ( ! stack.isEmpty() ) return stack;
            if ( stack == null ) return null;
            for ( int i = 0; i < batchSize(); i++ ) {
                Object anObject = aClass.newInstance();
                stack.add( anObject );
            }
        } catch (Exception ex) {
            error( this, "Couldn't refill stack for class: " + aClass.getName(), ex );
            return null;
        }
        
        return stack;
    }
    
    public int batchSize() { return _batchSize; }
    
    public void setBatchSize(int batchSize) {
        this._batchSize = batchSize;
    }
    
    public static class StatStore {
        
        static StatStore _defaultStore = null;
        PSDict _classToStats = new PSDict();
        
        public StatStore() {
            _classToStats = new PSDict();
        }
        
        public static StatStore store() {
            if ( _defaultStore == null ) {
                _defaultStore = new StatStore();
            }
            return _defaultStore;
        }
        
        void resetStats() { _defaultStore = null; }
        
        Stat addStatForClass( Class aClass ) {
            Stat aStat = new Stat( aClass );
            _classToStats.put( aClass, aStat );
            
            return aStat;
        }
        
        Stat statForClass( Class aClass ){
            Stat aStat = (Stat )_classToStats.get( aClass );
            if (aStat == null)
                aStat = addStatForClass( aClass );
            
            return aStat;
        }
        
        void incrAllocsForClass( Class aClass ) {
            Stat aStat = statForClass( aClass );
            aStat.incrAllocs();
        }
        
        void incrRecyclesForClass( Class aClass ) {
            Stat aStat = statForClass( aClass );
            aStat.incrRecyles();
        }
        
        String printMsg() {
            StringBuffer buffer = new StringBuffer();
            Iterator iter = _classToStats.keySet().iterator();
            while( iter.hasNext() ) {
                Class aClass = (Class )iter.next();
                Stat aStat = (Stat )_classToStats.get( aClass );
                buffer.append( "\n" );
                buffer.append( aClass.getName() ).append( " [" + aStat._allocs );
                buffer.append( ", " ).append( aStat._recycles ).append( "]");
            }
            return buffer.toString();
        }
        
        String printConditionalMsg() {
            StringBuffer buffer = new StringBuffer();
            Iterator iter = _classToStats.keySet().iterator();
            while( iter.hasNext() ) {
                Class aClass = (Class )iter.next();
                Stat aStat = (Stat )_classToStats.get( aClass );
                if ( ( aStat._allocs.intValue() - aStat._recycles.intValue() ) <= 1 ) continue;
                buffer.append( "\n" );
                buffer.append( aClass.getName() ).append( " [" + aStat._allocs );
                buffer.append( ", " ).append( aStat._recycles ).append( "]");
            }
            return buffer.toString();
        }
        
        public void debugStats() {
            String logMsg =  printMsg();
            
            if ( logMsg.length() > 0 )
                logger().debug( logMsg );
        }
        
        void shoutStats() {
            String logMsg =  printConditionalMsg();
            
            if ( logMsg.length() > 0 )
                logger().shout( printConditionalMsg() );
        }
        
    }
    
    static class Stat {
        
        Class _class;
        PSInt _allocs = PSInt.with(0);
        PSInt _recycles = PSInt.with(0);
        
        public Stat( Class aClass ) {
            _class = aClass;
        }
        
        public void incrAllocs()  { _allocs   = _allocs.incr(); }
        public void incrRecyles() { _recycles = _recycles.incr(); }
        
    }
    
    
}
