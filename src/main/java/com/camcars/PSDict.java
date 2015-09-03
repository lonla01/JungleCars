
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

public class PSDict extends Hashtable {
    
    private static Delegate _delegate = null;
    
    static {
        setDelegate( new Delegate() );
    }
    
    public PSDict() { }
    
    public PSDict(int capacity) {
        super(capacity);
    }
    
    public PSDict(Map map) {
        super(map);
    }
    
    public static Delegate delegate() {
        if ( _delegate == null ) {
            _delegate = new Delegate();
        }
        
        return _delegate;
    }
    
    public static void setDelegate(Delegate delegate) {
        _delegate = delegate;
    }
    
    public PSLogger logger() { return PSObject.logger(); }
    
    ///   F O R W A R D I N G   ////////////////////////////////////////////////
    public Object put( Object key, Object obj ) {
        return super.put( key, delegate().replaceObject( key, obj ) );
    }
    
    public Object get( Object key ) {
        return delegate().originalObject( super.get( key ) );
    }
    
    public String toPlist() {
        return delegate().toPlist( this );
    }
    
    public String toPrint() {
        return delegate().toPrint( this );
    }
    
    public String toString() {
        return delegate().toString( this );
    }
    
    public void encodeWithCoder( PSPlistCoder.Writer appender ) {
        delegate().encodeWithCoder( this, appender );
    }
    
    ///   N E S T E D   C L A S S E S   ////////////////////////////////////////
    public static class TMap extends TreeMap {
        
        public TMap() { }
        
        public TMap(Map map) {
            super(map);
        }
        
//        public static Delegate delegate() { return PSDictionary.delegate(); }
        
        ///   F O R W A R D I N G   ////////////////////////////////////////////////
        public Object put( Object key, Object obj ) {
            return super.put( key, delegate().replaceObject( key, obj ) );
        }
        
        public Object get( Object key ) {
            return delegate().originalObject( super.get( key ) );
        }
        
        public String toPlist() {
            return delegate().toPlist( this );
        }
        
        public String toString() {
            return delegate().toString( this );
        }
        
        public String toPrint() {
            return delegate().toPrint( this );
        }
        
        public void encodeWithCoder( PSPlistCoder.Writer appender ) {
            delegate().encodeWithCoder( this, appender );
        }
        
    } /// End TMap
    
    
    public static class IMap extends PSDict {
        
        public IMap() { }
        
        public IMap(Map map) {
            super(map);
        }
        
        ///   F O R W A R D I N G   ////////////////////////////////////////////////
        public Object put( Object key, Object obj ) {
            Object replacement;
            PSArray indices;
            
//            if ( key instanceof PSString ) {
//                PSString str = (PSString )key;
//                if ( str.length() < 2 )
//                    logger().error( this, "Invalid key: " + str );
//            }
            replacement = delegate().replaceObject( key, obj );
            indices = (PSArray )super.get( key );
            if ( indices == null ) {
                indices = new PSArray();
            }
            if ( ! indices.contains( obj ) ) {
                indices.add( obj );
            }
            
//            PSObject.logger().verbose( this, "key="+key + " obj="+obj + " rep=" + indices );
            
            return super.put( key, indices );
        }
        
        public Object get( Object key ) {
            Object original;
            PSArray origArray;
            
            origArray = (PSArray )delegate().originalObject( super.get( key ) );
            PSObject.logger().debug( this, "key=" + key + " orig=" + origArray );
            if ( origArray == null ) {
                PSObject.logger().error( this, "Null entry for key:[" + key + "]" );
                return null;
            }
            original = origArray.lastElement();                      
            
            return original;
        }
        
        public Object firstIndice( Object key ) {
            //PSObject.logger().debug( this, "firstIndice of key: " + key );
            PSArray array = allIndices( key );
            

            Object obj = (array == null || array.isEmpty() ) ? null : array.firstElement();
            //PSObject.logger().debug( this, "The First Indice is " + obj );
            
            return obj;
        }
        
        public Object lastIndice( Object key ) {
            PSArray array = allIndices( key );
            
            return (array == null) ? null : array.lastElement();
        }
        
        public PSArray allIndices( Object key ) {
            return (PSArray )delegate().originalObject( super.get( key ) );
        }
        
        public int indicesCount( Object key ) {
            PSArray array = allIndices( key );
            
            return (array == null) ? -1 : array.size();
        }
        
        public boolean keyHasIndice( Object key, PSInt anIndice ) {
            return allIndices( key ).contains( anIndice );
        }
        
        public String toPlist() {
            return delegate().toPlist( this );
        }
        
        public String toString() {
            return delegate().toString( this );
        }
        
        public void encodeWithCoder( PSPlistCoder.Writer appender ) {
            delegate().encodeWithCoder( this, appender );
        }
        
    } /// End IMap
    
    public static class StringIndex extends Hashtable {
        
        private int[]   _elemCount;
        private int[][] _indices;
        
        public StringIndex( String aString ) {
            int[] indexStr = indexStructure( aString );
            initIndices( indexStr );
            initElemCount();
        }
        
        public StringIndex( char[] chars ) {
            int[] indexStr = indexStructure( chars );
            initIndices( indexStr );
            initElemCount();
        }
        
        private void initIndices( int[] indexStr ) {
            _indices = new int[ PSChar.MAX_UTF8_CODE ][];
            
            for( int i=0; i < _indices.length; i++ ) {
                int indexCount = indexStr[i];
                if ( indexCount == -1 ) 
                    _indices[i] = null;
                else
                    _indices[i] = new int[ indexCount ];
            }
        }
        
        private void initElemCount() {
            _elemCount = new int[ PSChar.MAX_UTF8_CODE ];
            
            for ( int i=0; i < _elemCount.length; i++ ) {
                _elemCount[i] = 0;
            }
        }
        
        private int[] indexStructure( String aString ) {
            int[] indexStr = initIndexStructure();
            
            for ( int i=0; i < aString.length(); i++ ) {
                char aChar = aString.charAt(i);
                updateIndexForChar( indexStr, aChar );
            }
            
            return indexStr;
        }
        
        private int[] indexStructure( char[] chars ) {
            int[] indexStr = initIndexStructure();
            
            for ( int i=0; i < chars.length; i++ ) {
                char aChar = chars[i];
                updateIndexForChar( indexStr, aChar );
            }
            
            return indexStr;
        }

        private int[] initIndexStructure() {
            int[] indexStr = new int[ PSChar.MAX_UTF8_CODE ];
            
            for ( int i=0; i < indexStr.length; i++ ) {
                indexStr[i] = -1;
            }
            
            return indexStr;
        }

        private void updateIndexForChar( int[] indexStr, char aChar  ) {
            int indCount = indexStr[ (int )aChar ];
            
            indCount = ( indCount == -1 ) ? 1 : indCount + 1;
            indexStr[ (int )aChar ] = indCount;
        }
        
        public Object put( Object key, Object obj ) {
            int code = ( (PSChar )key ).charCode();
            int index = ( (PSInt )obj ).val();
            int[] indices = (int[] )_indices[ code ];
            
            if ( indices == null ) {
                indices = new int[1000];
            }
            indices[ _elemCount[ code ] ] = index;
            _elemCount[ code ]++;
            
            return null;
        }
        
        public Object put( PSChar aChar, int index ) {
            int code = aChar.charCode();
            int[] indices = (int[] )_indices[ code ];
            
            if ( indices == null ) {
                indices = new int[1000];
            }
            indices[ _elemCount[ code ] ] = index;
            _elemCount[ code ]++;
            
            return null;
        }
        
        public Object firstIndice( Object key ) {
            int code = ( (PSChar )key ).charCode();
            int first = allIndicesArray( key )[ _elemCount[ 0 ] ];
            
            return PSInt.with( first );
        }
        
        public Object lastIndice( Object key ) {
            int code = ( (PSChar )key ).charCode();
            int last = allIndicesArray( key )[ _elemCount[ code ] ];
            
            return PSInt.with( last );
        }
        
        public int[] allIndicesArray( Object key ) {
            int code = ( (PSChar )key ).charCode();
            return _indices[ code ];
//            return (PSArray )delegate().originalObject( super.get( key ) );
        }
        
        public PSArray allIndices( Object key ) {
            int[] ind = allIndicesArray( key );
            PSArray all;
            
            if ( ind == null ) return PSArray.EmptyArray;
//            all = new PSArray( ind.length );            
//            for ( int i=0; i < ind.length; i++ ) {
//                all.add( PSInt.with( i ) );
//            }
            all = PSInt.intsWith( ind );
            
            return all;
        }
        
        public boolean keyHasIndice( Object key, PSInt anIndice ) {
            int[] allInd = allIndicesArray( key );
            
            for ( int i=0; i < allInd.length; i++ ) {
                if ( anIndice.val() == allInd[i] ) return true;
            }
            
            return false;
        }
        
    } /// End StringIndex
    
    public static class Delegate {
        
        private static final PSDict.NullValue NULL_VALUE = new PSDict.NullValue();
//                PSPlistCoder.NULL_VALUE;
        
        public Object replaceObject( Object key, Object obj ) {
            return (obj == null) ? NULL_VALUE : obj;
        }
        
        public Object originalObject( Object obj ) {
            if (obj instanceof PSDict.NullValue) return null;
            else
                return obj;
        }
        
        public String toPlist( Map dict ) {
            Object obj;
            PSPlistCoder.Writer buffer = new PSPlistCoder.Writer();
            buffer.encodeDict( (Map )dict );
            
            return buffer.toString();
        }
        
        public void encodeWithCoder( Map dict, PSPlistCoder.Writer appender ) {
            appender.encodeDict( (Map )dict );
        }
        
        private void _appendKeyValue( PSBuffer buffer, Object key, Object val ) {
            buffer.append("[").append( key );
            buffer.append("] = [").append( val );
            buffer.append("]; ");
        }
        
        private void _appendKeyValue_toPrint( PSBuffer buffer,
                Object key, Object val ) {
            if ( key instanceof PSString )
                key = ((PSString )key).toPrint();
            if ( val instanceof PSString )
                val = ((PSString )val).toPrint();
            _appendKeyValue( buffer, key, val );
        }
        
        public String toString( Map dict ) {
            PSBuffer buffer = PSObject.pool().bufferInstance();
            
            buffer.append( dict.getClass().getName() ).append("{ ");
            Iterator iter = dict.keySet().iterator();
            while( iter.hasNext() ) {
                Object key = iter.next();
                Object val = dict.get( key );
                _appendKeyValue( buffer, key, val );
            }
            buffer.append(" }");
            String result = buffer.toString();
            PSObject.pool().recycleInstance( buffer );
            
            return result;
        }
        
        public String toPrint( Map dict ) {
            PSBuffer buffer = PSObject.pool().bufferInstance();
            
            buffer.append( dict.getClass().getName() ).append("{ ");
            Iterator iter = dict.keySet().iterator();
            while( iter.hasNext() ) {
                Object key = iter.next();
                Object val = dict.get( key );
                _appendKeyValue_toPrint( buffer, key, val );
            }
            buffer.append(" }");
            String result = buffer.toString();
            PSObject.pool().recycleInstance( buffer );
            
            return result;
        }
        
    } /// End Delegate
    
    /**
     * This class is not currently in use. Its behaviour is integrated in IMap.
     */
    public static class IndexDelegate extends Delegate {
        
        public Object replaceObject( Object key, Object obj ) {
            Object replacement;
            PSArray repArray;
            
            replacement = super.replaceObject( key, obj );
            repArray = new PSArray();
            repArray.add( replacement );
            
            PSObject.logger().debug( this, "key=" + key + " obj=" + obj + " rep=" + repArray );
            
            return repArray;
        }
        
        public Object originalObject( Object obj ) {
            Object original;
            PSArray origArray;
            
            origArray = (PSArray )super.originalObject( obj );
            original = origArray.firstElement();
            PSObject.logger().debug( this, "obj=" + obj + " orig=" + origArray );
            
            return original;
        }
        
    } /// End IndexDelegate
    
    public static class NullValue extends Object {
        
        public void takePropertyDict( PSDict dict ) { }
        
    }
    
}