package com.camcars;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.Query;

public class JCFetchRequest extends PSRoot {
	
	private String _entityName;
	private Session _session;
	private String _predicate;
	private Integer _firstResult = null;
	private Integer _maxResults = null;
	private String _parameter = null;
	private Object _value = null;

 
	public JCFetchRequest( Session session, String entityName ) {		
		_entityName = entityName;
		_session = session;		
	}
	
	public String getEntityName() {
		return _entityName;
	}
	
	public String getStatement() {
		return "from " + getEntityName() + getPredicate();
	}
	
	public Session getSession() {
		return _session;
	}
	
	public String getPredicate() {
		return (_predicate == null) ? "" : " WHERE " + _predicate;
	}
	
	public void setPredicate( String predicate ) {
		_predicate = predicate;
	}

    public void setPredicateBindings( Map bindings ) {

        StringBuilder sb = new StringBuilder();
        Collection predComponents = new ArrayList();

        if ( bindings == null || bindings.size() == 0 ) return;

        int index = 0;
        Iterator keyIter = bindings.keySet().iterator();
        while( keyIter.hasNext() ) {
            String aKey = (String )keyIter.next();
            Object aValue = bindings.get( aKey );
            if ( index++ != 0 ) sb.append(" AND ");
            if ( aValue instanceof String ) {
                sb.append( aKey + " like " + "'%" + aValue + "%'" );
            } else if ( aValue instanceof PSRange ) {
                PSRange r = (PSRange )aValue;
                sb.append( aKey + " >= '" + r.start() + "' AND " + aKey + " <= '" + r.end() + "'" );
            }
        }

        setPredicate( sb.toString() );
    }
	
	public String getParameter() { return _parameter; }	
	public void setParameter( String parameter ) {
		_parameter = parameter;
	}
	
	public Object getValue() { return _value; }
	public void setValue( Object value ) {
		_value = value;
	}
	
	public Integer getFirstResult() { return _firstResult; }
	public void setFirstResult( Integer firstResult ) {
		_firstResult = firstResult;
	}
	
	public Integer getMaxResults() { return _maxResults; }
	public void setMaxResults( Integer maxResults ) {
		_maxResults = maxResults;
	}
	
	protected Object createQuery() {
		return (Query )getSession().createQuery( getStatement() );
	}
	
	@SuppressWarnings("unchecked")
	public List<JCEntity> listEntities() {
		
   		List<JCEntity> modelList = null;
   		
		try {
			Query query = (Query )createQuery();  
			if ( getFirstResult() != null ) {
				query.setFirstResult( getFirstResult().intValue() );
			}
			if ( getMaxResults() != null ) {
				query.setMaxResults( getMaxResults().intValue() );
			}
		    modelList = (List<JCEntity> )query.list();      		      		    
		} catch (Exception e) {
            logger().error( this, "Statement=[" + getStatement() + "]" );
			logger().error( this, "Error fetching entities of type: " + _entityName );
		}
		
		return modelList;
		
	}
	
	
}
