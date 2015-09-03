/**
 * PSVisitor.java
 *
 * Created on 29 juillet 2009, 13:48
 *
 * <p>Title: Prophetic Studies </p>
 * <p>Description: Bible study tools and resources </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Private: </p>
 * @author Serge Patrice LONLA
 * @version 1.0P
 */
package com.camcars;

public abstract class PSVisitor extends PSObject {
    
    Object _result = null;

    public PSVisitor( Object result ) {
        setResult( result );
    }
    
    public void resetState() { setResult(null); }
    
    public void visitMe( Object aHost ) {
        if ( aHost == null ) {
            logger().error( this, "Cannot visit a null host" );
            return;
        }
        if ( aHost instanceof PSInt )
            visitMe( (PSInt )aHost );
        else if ( aHost instanceof PSChar ) 
            visitMe( (PSChar )aHost );
        else if ( aHost instanceof String ) 
            visitMe( (String )aHost );
        else if ( aHost instanceof PSString ) 
            visitMe( (PSString )aHost );
        else if ( aHost instanceof PSObject )
            visitMe( (PSObject )aHost );                
        else if ( aHost instanceof PSArray ) 
            visitMe( (PSArray )aHost );
        else
            throw new AbstractMethodInvocation( "visitMe( " +
                    aHost.getClass().getName() + " )" );
    }
    
//    public void visitMe( PSCoding.KeyValueCoding aHost ) {
//        throw new AbstractMethodInvocation( "visitMe( PSCoding.KeyValueCoding )" );
//    }
    
    public void visitMe( PSObject aHost ) {
        throw new AbstractMethodInvocation( "visitMe( PSObject )" );
    }
    
    public void visitMe( PSInt aHost ) {
        throw new AbstractMethodInvocation( "visitMe( PSInt )" );
    }
    
    public void visitMe( String aHost ) {
        throw new AbstractMethodInvocation( "visitMe( String )" );
    }
    
    public void visitMe( PSString aHost ) {
        throw new AbstractMethodInvocation( "visitMe( PSString )" );
    }
    
    public void visitMe( PSChar aHost ) {
        throw new AbstractMethodInvocation( "visitMe( PSChar )" );
    }
    
    public void visitMe( PSArray aHost ) {
        throw new AbstractMethodInvocation( "visitMe( PSArray )" );
    }
    
    public void setResult( Object result ) { _result = result; }
    public Object       result()        { return _result; }  
    
//    public PSCoding.KeyValueCoding     kvCodingResult(){ 
//        return (PSCoding.KeyValueCoding    )result(); 
//    }
    public PSObject     psObjectResult(){ return (PSObject    )result(); }
    public PSInt        intResult()     { return (PSInt       )result(); }
    public String       stringResult()  { return (String      )result(); }
    public PSString     psStringResult(){ return (PSString    )result(); }
    public PSArray      arrayResult()   { return (PSArray     )result(); }
    public PSChar       charResult()    { return (PSChar      )result(); }
    
   

    
}
