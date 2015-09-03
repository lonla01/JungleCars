package com.camcars;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class JCSQLFetchRequest extends JCFetchRequest {
	
	private String _sqlString;
	
	public JCSQLFetchRequest( Session session, String entityName, String sqlString ) {		
		super( session, entityName );
		_sqlString = sqlString;
	}
	
	@Override
	public String getStatement() {
		return _sqlString;
	}
		
	@Override
	protected Object createQuery() {
		
		SQLQuery query = null;
		
		try {
			query = getSession().createSQLQuery( getStatement() );
			query.addEntity( Class.forName( "com.camcars." + getEntityName() ) );
			if ( getParameter() != null )
				query.setParameter( getParameter(), getValue() );
		} catch( Exception e ) {
			query = null;
			System.out.println( "couldn't create query: " + getStatement() + " for entity: " + getEntityName() );
			e.printStackTrace();
		}
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> listSQLResults() {
		
   		List<Object> modelList = null;
   		
		try {
			Query query = (Query )createQuery();  
			if ( getFirstResult() != null ) {
				query.setFirstResult( getFirstResult().intValue() );
			}
			if ( getMaxResults() != null ) {
				query.setMaxResults( getMaxResults().intValue() );
			}
		    modelList = (List<Object> )query.list();      		      		    
		} catch (Exception e) {
			System.out.println( "Error fetching entities of type: " + getEntityName() );
			e.printStackTrace();
		} 
		
		return modelList;
		
	}
	
	/**
	 * For results that are not of JCEntity type (ex. string results) we cannot use Hibernate to parse 
	 * them. So in such cases we fallback to direct JDBC access of the database.
	 * This method is called for example to get a list of distinct car type existing in the database.
	 * @return
	 */
	public List<Object> listJDBCResults() {

		List<Object> results = new ArrayList<Object>();
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn = null;
		Properties conn_info = new Properties( );
		conn_info.put( "user", "junglecars" );
		conn_info.put( "password", "junglecars" );


		try {

			Class.forName( "com.mysql.jdbc.Driver" ) ;
			conn = (Connection) DriverManager.getConnection( "jdbc:mysql://localhost:3306/junglecars", conn_info ) ;
	
			for ( SQLWarning warn = conn.getWarnings(); warn != null; warn = warn.getNextWarning() ) {
				System.out.println( "SQL Warning:" ) ;
				System.out.println( "State  : " + warn.getSQLState()  ) ;
				System.out.println( "Message: " + warn.getMessage()   ) ;
				System.out.println( "Error  : " + warn.getErrorCode() ) ;
			}

			stmt = (Statement) conn.createStatement() ;
			rs = stmt.executeQuery( getStatement() ) ;

			while( rs.next() ) {
				results.add( rs.getString(1) );
			}

		} catch( SQLException se ) {

			while( se != null ) {
				System.out.println( "State  : " + se.getSQLState()  );
				System.out.println( "Message: " + se.getMessage()   );
				System.out.println( "Error  : " + se.getErrorCode() );

				se = se.getNextException() ;
			}

		} catch( Exception e ) {
			System.out.println( e ) ;
		} finally {
			
			try {
				rs.close() ;
				stmt.close() ;
				conn.close() ;
			} catch ( Throwable e ) {

			}
			
		}
		
		return results;
	}

}

