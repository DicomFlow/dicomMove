package br.ufpb.dicomflow.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ufpb.dicomflow.bean.Persistent;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.util.Constants;
import br.ufpb.dicomflow.util.Util;


public class HibernatePersistentServiceImpl extends HibernateDaoSupport  implements PersistentService {

    /**
	 * Default constructor.
	 */
	public HibernatePersistentServiceImpl() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#onlySave(java.lang.Object)
	 */
	public Integer onlySave(Object object){
		Session session = getSession();	
		Transaction trans = null;
		Serializable x = null;
		try {
			trans = session.beginTransaction();		
			x = session.save(object);				
			trans.commit();
			session.flush();					
		} catch (Exception e) {
			if (trans!=null) 		
				trans.rollback();		
			e.printStackTrace();
		}finally {
		    session.close();
		}
	    return (Integer) x;  
	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#saveOrUpdate(java.lang.Object)
	 */
	public void saveOrUpdate(Object object){
		Session session = getSession();	
		Transaction trans = null;
		Serializable x = null;
		try {
			trans = session.beginTransaction();		
			if (!((Persistent)object).saveOnDb()) {
				logger.debug("enrei no save");
				x =  session.save(object);
//				((Persistent) object).setId(((Integer)x).intValue());
			}else {
				logger.debug("entrei no update");
				session.update(object);
			}		
			
			trans.commit();
			session.flush();					
			
			
		} catch (Exception e) {
			if (trans!=null) 		
				trans.rollback();
			e.printStackTrace();
		}finally {
		    session.close();
		}
	}
	
	


	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#remove(java.lang.Object)
	 */
	public void remove(Object object){
		Session session = getSession();	
		Transaction trans = null;
		try {
			trans = session.beginTransaction();				
			session.delete(object);
			trans.commit();
			session.flush();					
		} catch (Exception e) {
			if (trans!=null) 		
				trans.rollback();
			e.printStackTrace();
		}finally {
		    session.close();
		}
		
	}
	

	public Integer selectCount(Class object){
		Session session = getSession();	
		Integer quantity = new Integer(0);
		try {
			Query query = session.createQuery("select count(*) from "+ object.getName());
			Util.getLogger(this).debug("Query -- > " + query);
			quantity = (Integer) query.uniqueResult();
			session.flush();					
		} catch (Exception e) {			
			e.printStackTrace();
		}finally {
		    session.close();
		}
		Util.getLogger(this).debug("quantidade = " + quantity);
		return quantity;
	}
	
	public Long selectCount(String field ,Object value, Class table){
		Session session = getSession();	
		Long quantity = new Long(0);
		try {
			Query query = session.createQuery("select count(*) from "+ table.getName()+ " o where o."+field+"= :field" );
			query.setEntity("field", value);
			Util.getLogger(this).debug("Query -- > " + query);
			quantity = (Long) query.uniqueResult();
			session.flush();					
		} catch (Exception e) {			
			e.printStackTrace();
		}finally { 
			session.clear();
		    session.close();
		}
		Util.getLogger(this).debug("quantidade = " + quantity);
		return quantity;
	}
	
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public List selectAll(String param, Object value, Class type) {
	    Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    criteria.add(Restrictions.eq(param, value));
	    try {
	        List list  = criteria.list(); 
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}

	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.Class)
	 */
	public List selectAll(Class type){
	
		Session session = getSession();	
		
		try {			
			Criteria cri = session.createCriteria(type);		
			List list = cri.list();			
			session.flush();			
			return list;	
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally {
		    session.close();
		}	
		return new ArrayList();	
	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public List selectAll(String param, Object value, Class type, int maxResult) {
	    Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    criteria.add(Restrictions.eq(param, value));
	    criteria.setMaxResults(maxResult);
	    try {
	        List list  = criteria.list(); 
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}


	/**
	 * @return
	 */
	public Session openSession() {		
		return this.getSession();
	}


	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#select(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public Object select(String param, Object value, Class type) {
		List list = selectAll(param,value,type);
		return (list !=null && !list.isEmpty())? list.get(0):null;
	}
	
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.util.List, java.lang.Class)
	 */
	public List selectAll(String param, List values, Class type) {
		List result = new ArrayList();
		
		Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    for (int i = 0; i < values.size(); i++) {
	    	criteria = criteria.add(Restrictions.in(param, values));	
		}
	    try {
	        List list  = criteria.list(); 
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally { 
        	session.clear();
		    session.close();
		}  
        return new ArrayList();
	}
	
	public List selectAllIn(String param, List values, Class type) {
		
		String query = "from " + type.getName() + " type1 ";
		
		if(values != null && values.size() != 0){
			query +=    " where type1." + param + " in ( ";
		
			Iterator it = values.iterator();
			if(it.hasNext()){
				Object value = it.next();
				query += "'" + value.toString()+ "'";
			}
			while (it.hasNext()) {
				Object value = (Object) it.next();
				query += ",'" + value.toString() +"'";
				
			}
			query +=")";
		}              
		               
		List result = new ArrayList();
		
		Session session = this.getSession();
		try {
			Query consulta = session.createQuery(query);
	        List list  = consulta.list();
	        return list;
		} catch (HibernateException e) {
			this.logger.error("Error retrieving objects", e);
	        e.printStackTrace();
		} finally {
			session.clear();
			session.close();	
		}		                
		return new ArrayList();
	}
	
	public List selectAllNotIn(String param, List values, Class type) {
		
String query = "from " + type.getName() + " type1 ";
		
		if(values != null && values.size() != 0){
			query +=    " where type1." + param + " not in ( ";
		
			Iterator it = values.iterator();
			if(it.hasNext()){
				Object value = it.next();
				query += "'" + value.toString()+ "'";
			}
			while (it.hasNext()) {
				Object value = (Object) it.next();
				query += ",'" + value.toString() +"'";
				
			}
			query +=")";
		}              
		               
		List result = new ArrayList();
		
		Session session = this.getSession();
		try {
			Query consulta = session.createQuery(query);
	        List list  = consulta.list();
	        return list;
		} catch (HibernateException e) {
			this.logger.error("Error retrieving objects", e);
	        e.printStackTrace();
		} finally {
			session.clear();
			session.close();	
		}		                
		return new ArrayList();
	}
	
	/**
	 * Metodo que prepara uma query de busca em formato String 
     * @param param o campo que será comparado na busca
     * @param values os valores a serem comparados na busca
     * @param type o tipo da classe onde vai ser buscado os resultados
     * @return
     */
    private String prepareSelect(String param, List values, Class type) {
        String query = "from " + type.getName() + " type1 ";
        Iterator it =  values.iterator();
        if(it.hasNext()){
            Object element = (Object) it.next();
            query +="where type1." + param +"=" + element.toString();
        }
        while (it.hasNext()) {
            Object element = (Object) it.next();
            query +=" or type1."+ param + "=" +element.toString();    
            
        }
        return query;
    }
    
    public Object selectByParams(Object[] params, Object[] values, Class type) {
    	List list = selectAllByParams(params, values, type);
    	return (list !=null && !list.isEmpty())? list.get(0):null;
    	
    }
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.util.List, java.lang.Class)
	 */
    public List selectAllByParams(Object[] params, Object[] values, Class type) {
		
		List result = new ArrayList();
		if(params.length != values.length){
			return result;
		}
		
		Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    for (int i = 0; i < params.length; i++) {
	    	criteria.add(Restrictions.eq((String) params[i], values[i]));	
		}
	    try {
	        List list  = criteria.list(); 
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally { 
        	session.clear();
		    session.close();
		}  
        return new ArrayList();

	}
	

    /* (non-Javadoc)
     * @see br.org.paqtc.siri.service.dao.PersistentService#selectLike(java.lang.String[], java.lang.Object, java.lang.Class)
     */
    public List selectLike(String[] param, Object value, Class type) throws Exception {
    	Session session = this.getSession();

	    try {
	         
	        String query = "from " + type.getName();
	        if(param.length != 0){
	            query += " where " + param[0] + " like '%" + value + "%'";  
	        }
	        for (int i = 1; i < param.length; i++) {
                query += " or " + param[i] + " like '%" + value + "%'";
            }
	        Query consulta = session.createQuery(query);
	        List list  = consulta.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }finally{
        	session.close();
        }    

	}
    
    public List selectLike(String[] param, Object[] value, Class type) throws Exception {
    	Session session = this.getSession();
    	
	    try {
	    	if(param.length != value.length){
	    		throw new HibernateException("A lista de parametros e valores devem ter o mesmo tamanho");
	    	}
	         
	        String query = "from " + type.getName();
	        if(param.length != 0){
	            query += " where " + param[0] + " like '%" + value[0] + "%'";  
	        }
	        for (int i = 1; i < param.length; i++) {
                query += " or " + param[i] + " like '%" + value[i] + "%'";
            }
	        Query consulta = session.createQuery(query);
	        List list  = consulta.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }finally{
        	session.close();
        }    

	}
    
    
    
    public List selectAllObjectsByJoinTable(String field, Object value, Class joinTable, String result){
    	Session session = this.getSession();
    	Transaction trans = null;
    	try{
    		trans =  session.beginTransaction();
    		Query query = session.createQuery("select joinTable."+ result + " from " + joinTable.getName()+ " joinTable where joinTable."+field+" = :value");
    		query.setEntity("value", value);
    		List list = query.list();
    		trans.commit();
    		session.flush();
    		return list;
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }finally{
	    	session.close();
	    }   
	    return new ArrayList();
    	
    }
    
    public List selectAllOrderBy(Class type,String orderType, String order){
    	
		Session session = getSession();	
		
		try {			
			Criteria cri = session.createCriteria(type);
			if(orderType.equals(PersistentService.ASC)){
		    	cri.addOrder(Order.asc(order));
		    }
		    if(orderType.equals(PersistentService.DESC)){
		    	cri.addOrder(Order.desc(order));
		    }
			List list = cri.list();			
			session.flush();			
			
			return list;	
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally {
		    session.close();
		}	
		return new ArrayList();	
	}
    
    
    
    public List selectOrderBy(String param, Object value, Class type,String orderType, String order){
	    Session session = this.getSession();
	    List list = new ArrayList();
        Criteria criteria = session.createCriteria(type);
	    criteria.add(Restrictions.eq(param, value));
	    if(orderType.equals(PersistentService.ASC)){
	    	criteria.addOrder(Order.asc(order));
	    }
	    if(orderType.equals(PersistentService.DESC)){
	    	criteria.addOrder(Order.desc(order));
	    }
	    
	    try {
	        list  = criteria.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        } finally{
        	session.close();
        }

	}
    
    public List selectOrderByWithParams(String param, List values, Class type,String orderType, String order){
	    Session session = this.getSession();
	    List list = new ArrayList();
        Criteria criteria = session.createCriteria(type);
        Iterator it = values.iterator();
        while (it.hasNext()) {
			Object object = (Object) it.next();
			criteria.add( Restrictions.eq(param, object));
		}
	    
	    if(orderType.equals(PersistentService.ASC)){
	    	criteria.addOrder(Order.asc(order));
	    }
	    if(orderType.equals(PersistentService.DESC)){
	    	criteria.addOrder(Order.desc(order));
	    }
	    
	    try {
	        list  = criteria.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        } finally{
        	session.close();
        }

	}
    
    
    public List selectUniqueResultOrderBy(String param, Object value, Class type,String orderType, String order){
	    Session session = this.getSession();
	    List list = new ArrayList();
        Criteria criteria = session.createCriteria(type);
	    criteria.add(Restrictions.eq(param, value));
	    if(orderType.equals(PersistentService.ASC)){
	    	criteria.addOrder(Order.asc(order));
	    }
	    if(orderType.equals(PersistentService.DESC)){
	    	criteria.addOrder(Order.desc(order));
	    }
	    
	    criteria.setMaxResults(1);
	    try {
	        list  = criteria.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        } finally{
        	session.close();
        }

	}
    
    public List selectAllObjectsByJoinTable(String field, List value,String field2, List value2, Class joinTable){
    	Session session = this.getSession();
    	Transaction trans = null;
    	try{
    		trans =  session.beginTransaction();
    		String queryString =prepareJoinTableQuery(field,value,field2, value2,joinTable);
    		Query query = session.createQuery(queryString);
    		for (int i = 0; i < value.size(); i++) {
    			query.setEntity("value"+i, value.get(i));
			}
    		query.setParameterList("list", value2);
    		List list = query.list();
    		trans.commit();
    		session.flush();
    		
    		return list;
	    } catch (Exception e1) {
	        
	    }finally{
	    	session.close();
	    }   
	    return new ArrayList();
    	
    }
    
    
    
    private String prepareJoinTableQuery(String field, List value,String field2, List value2, Class joinTable) {
    	StringBuffer query = new StringBuffer(" from " + joinTable.getName()+ " joinTable where ( " );
    	
    	
    	int count = 0;
    	Iterator valueIt = value.iterator();
    	if(valueIt.hasNext()){
    		valueIt.next();
    		query.append("joinTable."+field+" = :value"+count +" and joinTable." +field2 + " in ( :list )) " );
    		count++;
    	}
    	while(valueIt.hasNext()){
    		valueIt.next();
    		query.append(" or ( joinTable."+field+" = :value"+count +" and joinTable." +field2 + " in ( :list )) ");
    		count++;
    	}
		return query.toString();
	}
    
    public List selectAllWithInnerSelect(String field, Class type,String InnerField, Object innerValue, Class innerType){
    	Session session = this.getSession();
    	Transaction trans = null;
    	try{
    		trans =  session.beginTransaction();
    		String queryString =prepareInnerQuery(field, type, InnerField,innerValue,innerType);
    		Util.getLogger(this).debug(">>>>>>>>>>>>>>> "+  queryString);
    		Query query = session.createQuery(queryString);
    		query.setEntity("value", innerValue);
    		List list = query.list();
    		trans.commit();
    		session.flush();
    		
    		return list;
	    } catch (Exception e1) {
	        
	    }finally{
	    	session.close();
	    }   
	    return new ArrayList();
    	
    }
    
	private String prepareInnerQuery(String field, Class type, String innerField, Object innerValue, Class innerType) {
		StringBuffer query = new StringBuffer(" from " + type.getName()+ " fistType where fistType."+ field +" in ( " +
				"select secondType." +field + " from " + innerType.getName() + " secondType where secondType." + innerField +" = :value )" );
		return query.toString();
	}

	public void remove(String field, Integer value, Class entity){
		if(value == null){
			return;
		}
		Session session = getSession();	
		Transaction trans = null;
		try {
			trans = session.beginTransaction();				
			Query query = session.createQuery("delete from "+ entity.getName() + " where "+field+" = :value");
			query.setInteger("value", value.intValue());
			query.executeUpdate();
			trans.commit();
			session.flush();					
						
		} catch (HibernateException e) {
			if(trans != null){
				trans.rollback();
			}
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	
	public void removeByIds(String field, List<Integer> values, Class entity){
		if(values == null || values.isEmpty()){
			return;
		}
		Session session = getSession();	
		Transaction trans = null;
		try {
			trans = session.beginTransaction();				
			Query query = session.createQuery(prepareRemoveQuery(field,values, entity));
			Iterator<Integer> it = values.iterator();
			while(it.hasNext()){
				int value = it.next().intValue();
				query.setInteger("value"+value, value);
			}
			
			query.executeUpdate();
			trans.commit();
			session.flush();					
						
		} catch (HibernateException e) {
			if(trans != null){
				trans.rollback();
			}
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	

	private String prepareRemoveQuery(String field, List<Integer> values, Class entity) {
		StringBuffer query = new StringBuffer("delete from "+ entity.getName());
		Iterator<Integer> it = values.iterator();
		if(it.hasNext()){
			query.append(" where "+field+" = :value"+it.next().intValue());
		}
		
		while(it.hasNext()){
			query.append(" or " + field+ " = :value"+it.next().intValue());
		}
		
		return query.toString();
	}

	public void removeByEntities(String field, List values, Class entity){
		if(values == null || values.isEmpty()){
			return;
		}
		System.err.println("entrei no método que vai remover todos os registros selecionados!!!!!");
		Session session = getSession();	
		Transaction trans = null;
		try {
			trans = session.beginTransaction();				
			Query query = session.createQuery(prepareRemoveByEntityQuery(field,values, entity));
			Iterator it = values.iterator();
			int value = 0;
			while(it.hasNext()){
				Object valueObj = it.next();
				query.setEntity("value"+value, valueObj);
				value++;
			}
			
			query.executeUpdate();
			trans.commit();
			session.flush();					
						
		} catch (HibernateException e) {
			if(trans != null){
				trans.rollback();
			}
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	
	private String prepareRemoveByEntityQuery(String field, List values, Class entity) {
		StringBuffer query = new StringBuffer("delete from "+ entity.getName());
		Iterator it = values.iterator();
		int value = 0;
		if(it.hasNext()){
			it.next();
			query.append(" where "+field+" = :value" + value);
			value++;
		}
		
		while(it.hasNext()){
			it.next();
			query.append(" or " + field+ " = :value"+value);
			value++;
		}
		System.err.println("SQL --> " + query.toString());
		return query.toString();
	}
	
	public void remove(String field, Object value, Class entity){
		if(value == null ){
			return;
		}
		Session session = getSession();	
		Transaction trans = null;
		try {
			trans = session.beginTransaction();				
			Query query = session.createQuery("delete from "+ entity.getName() + " where "+field+" = :value");
			query.setEntity("value", value);
			query.executeUpdate();
			trans.commit();
			session.flush();					
						
		} catch (HibernateException e) {
			if(trans != null){
				trans.rollback();
			}
			e.printStackTrace();
		}finally{
			session.close();
		}
		
	}
	
	
	public List selectByDate(String param, Date value, int sign ,Class type ) throws HibernateException{
        List result = new ArrayList();
        Session session = getSession();
        try {
            
            Criteria criteria = session.createCriteria(type);
            switch (sign) {
            case Constants.LESS_THAN:
                criteria.add(Restrictions.lt(param,value));
                break;
            case Constants.GREATER_THAN:
                criteria.add(Restrictions.gt(param,value));
                break;   
            case Constants.EQUALS:
                criteria.add(Restrictions.eq(param,value));
                break;
            }
            result = criteria.list();
			session.flush();			
			
			return result;
        } catch (HibernateException e) {
            e.printStackTrace();
        }finally{
        	session.close();
        } 
        return result;
    }
	
	
	public List selectByDates(String param, Object value, String dateParam, Date start, Date finish ,Class type, String joinCollection ) throws HibernateException{
        List result = new ArrayList();
        Session session = getSession();
        try {
            
            Criteria criteria = session.createCriteria(type);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
           System.err.println("inicio " + start +"   fim  "+ finish);
            
            criteria.add(Restrictions.ge(dateParam,start));
            criteria.add(Restrictions.le(dateParam,finish));
            
            criteria.createCriteria(joinCollection).add(Restrictions.like(param, "%"+value+"%"));
            
            result = criteria.list();
			session.flush();			
			
			return result;
        } catch (HibernateException e) {
            e.printStackTrace();
        }finally{
        	session.close();
        } 
        return result;
    }
	
	
	public List selectPagingWithParams(Object[] params, Object[] values, int first, int max, Class type) throws Exception {
		Session session = getSession();	
	    try {
	        Criteria criteria = session.createCriteria(type);
	        
	        for (int i = 0; i < params.length; i++) {
	        	criteria.add(Restrictions.eq((String) params[i], values[i]));	
			}
	        
		    if(first >= 0 && max >= 0){
		        criteria.setFirstResult(first);
		        criteria.setMaxResults(max);
		    }
	        List list  = criteria.list();
			//trans.commit();
			session.flush();
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }  finally{
        	
				session.close();
			
        }

	}
	
	public List selectPaging(String param, Object value, int first, int max, Class type) throws Exception {
		Session session = getSession();	
		//Transaction trans = null;
	    try {
		    //List objects = null;
		   // trans = session.beginTransaction();
	    	
	        Criteria criteria = session.createCriteria(type);
	        criteria.add(Restrictions.eq(param, value));
		    if(first >= 0 && max >= 0){
		        criteria.setFirstResult(first);
		        criteria.setMaxResults(max);
		    }
	        List list  = criteria.list();
			//trans.commit();
			session.flush();
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }  finally{
        	
				session.close();
			
        }

	}
	
	public List selectPaging(int first, int max, Class type) throws Exception {
		Session session = getSession();	
		//Transaction trans = null;
	    try {
		    //List objects = null;
		   // trans = session.beginTransaction();
	    	
	        Criteria criteria = session.createCriteria(type);
	        
		    if(first >= 0 && max >= 0){
		        criteria.setFirstResult(first);
		        criteria.setMaxResults(max);
		    }
		    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	        List list  = criteria.list();
			//trans.commit();
			session.flush();
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }  finally{
        	
				session.close();
			
        }

	}
	
	public List selectPagingOrderBy(int first, int max, Class type, String orderType, String order) throws Exception {
		Session session = getSession();	
		//Transaction trans = null;
	    try {
		    //List objects = null;
		   // trans = session.beginTransaction();
	    	
	        Criteria criteria = session.createCriteria(type);
	        
			if(orderType.equals(PersistentService.ASC)){
				criteria.addOrder(Order.asc(order));
		    }
		    if(orderType.equals(PersistentService.DESC)){
		    	criteria.addOrder(Order.desc(order));
		    }
		    
		    if(first >= 0 && max >= 0){
		        criteria.setFirstResult(first);
		        criteria.setMaxResults(max);
		    }
	        List list  = criteria.list();
			//trans.commit();
			session.flush();
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        }  finally{
        	
				session.close();
			
        }

	}
	
	
	
	public List selectPagingOrderBy(int first, int max, String param, Object value, Class type,String orderType, String order){
	    Session session = this.getSession();
	    List list = new ArrayList();
        Criteria criteria = session.createCriteria(type);
	    criteria.add(Restrictions.eq(param, value));
	    if(orderType.equals(PersistentService.ASC)){
	    	criteria.addOrder(Order.asc(order));
	    }
	    if(orderType.equals(PersistentService.DESC)){
	    	criteria.addOrder(Order.desc(order));
	    }
	    
	    if(first >= 0 && max >= 0){
	        criteria.setFirstResult(first);
	        criteria.setMaxResults(max);
	    }
	    
	    try {
	        list  = criteria.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            throw new DataRetrievalFailureException("Error retrieving objects", e);
        } finally{
        	session.close();
        }

	}
	
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public List selectAllInnerJoin(String param, Object value, Class type, String fetch) {
	    
		Session session = this.getSession();
		Util.singleton().getLogger(this).debug("A QUERY LEFT JOIN= " + "select type from " + type.getName()  + " type inner join fetch type."+fetch+" where type."+param+"= :param" );
	    Query query = session.createQuery("select type from " + type.getName() + " type inner join fetch type."+fetch+" where type."+param+"= :param");
	    query.setParameter("param", value);
	    try {
	        List list  = query.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public List selectAllInnerJoin(Class type, String fetch) {
	    
		Session session = this.getSession();
		Util.singleton().getLogger(this).debug("A QUERY LEFT JOIN= " + "select type from " + type.getName()  + " type inner join fetch type."+fetch );
	    Query query = session.createQuery("select type from " + type.getName() + " type inner join fetch type."+fetch);
	    try {
	        List list  = query.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}
	
	
	public List selectAllOneToOne(Class projectType, Class otherType) {
	    
		Session session = this.getSession();
		Util.singleton().getLogger(this).debug("A QUERY NOT NULL = " + "select type from " + projectType.getName() + " type, "+ otherType.getName()+ " type2  where type.id=type2.id");
	    Query query = session.createQuery("select type from " + projectType.getName() + " type, "+ otherType.getName()+ " type2  where type.id=type2.id");
	    try {
	        List list  = query.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public Object selectObjectLoadLazyCollection(String param, Object value, Class type, String fetch) {
		Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    criteria.setFetchMode(fetch, FetchMode.JOIN);
	    criteria.add(Restrictions.eq(param, value));
	    List list = null;
	    try {
	        list  = criteria.list(); 
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}
        Util.singleton().getLogger(this).debug("list "+ list);
        return (list !=null && !list.isEmpty())? list:null;  
      }
	
	
	public Object selectObjectLoadLazyCollection(Class type, String fetch) {
		Session session = this.getSession();
	    Criteria criteria = session.createCriteria(type);
	    criteria.setFetchMode(fetch, FetchMode.JOIN);
	    List list = null;
	    try {
	        list  = criteria.list(); 
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}
        Util.singleton().getLogger(this).debug("list "+ list);
        return (list !=null && !list.isEmpty())? list:null;  
      }
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.service.dao.PersistentService#selectAll(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public List selectAllFullJoin(String param, Object value, Class type, String fetch, String fetch2) {
	    
		Session session = this.getSession();
		Util.singleton().getLogger(this).debug("A QUERY FULL JOIN= " + "select type from " + type.getName()  + " type inner join fetch type."+fetch+" left join fetch type."+fetch2+" where type."+param+"= :param" );
	    Query query = session.createQuery("select type from " + type.getName() + " type inner join fetch type."+fetch+" left join fetch type."+fetch2+" where type."+param+"= :param");
	    query.setParameter("param", value);
	    try {
	        List list  = query.list(); 
	        
	        return list;
        } catch (HibernateException e) {
            this.logger.error("Error retrieving objects", e);
            e.printStackTrace();
        } finally {
		    session.close();
		}  
        return new ArrayList();

	}

	public Object selectInnerJoin(String param, Object value, Class type, String fetch) {
		List list = selectAllInnerJoin(param,value,type, fetch);
		return (list !=null && !list.isEmpty())? list.get(0):null;
	}
	
	public Object selectFullJoin(String param, Object value, Class type, String fetch, String fetch2) {
		List list = selectAllFullJoin(param,value,type, fetch, fetch2);
		return (list !=null && !list.isEmpty())? list.get(0):null;
	}

	public void updatePrioridadeSessao(String[] fields, Object[] newValues, String[] params, Object[] values,Class classBean) {
		
		if(fields.length == 0 || newValues.length == 0 || params.length == 0 || values.length ==0){
			return;
		}
		if(fields.length != newValues.length || params.length != values.length ){
			return;
		}
		Session session = getSession();
        try {
            
            //Transaction trans = session.beginTransaction();
        	String querySql = "UPDATE "+ classBean.getName()+ " type1  SET "  ;
        	for (int i = 0; i < fields.length; i++) {
				querySql += " type1."+fields[i]+"= :field"+i;
				if ((i+1) < fields.length) {
					querySql += ", ";
				}
			}
        	
        	querySql += " WHERE ";
        	for (int i = 0; i < params.length; i++) {
				querySql += " type1."+params[i]+"= :param"+i;
				if ((i+1) < params.length) {
					querySql += " and ";
				}
			}
			Query query = session.createQuery( querySql);
			
			for (int i = 0; i < newValues.length; i++) {
				if (newValues[i] instanceof Boolean) {
					query.setBoolean("field"+i, (Boolean)newValues[i]);	
				}else if (newValues[i] instanceof Integer) {
					query.setInteger("field"+i, (Integer)newValues[i]);	
				}else if (newValues[i] instanceof String) {
					query.setString("field"+i, (String)newValues[i]);	
				}else if (newValues[i] instanceof Date) {
					query.setDate("field"+i, (Date)newValues[i]);	
				}else if (newValues[i] instanceof Double) {
					query.setDouble("field"+i, (Double)newValues[i]);	
				}else{
					query.setEntity("field"+i, newValues[i]);
				}
				
			}
			
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof Boolean) {
					query.setBoolean("param"+i, (Boolean)values[i]);	
				}else if (values[i] instanceof Integer) {
					query.setInteger("param"+i, (Integer)values[i]);	
				}else if (values[i] instanceof String) {
					query.setString("param"+i, (String)values[i]);	
				}else if (values[i] instanceof Date) {
					query.setDate("param"+i, (Date)values[i]);	
				}else if (values[i] instanceof Double) {
					query.setDouble("param"+i, (Double)values[i]);	
				}else{
					query.setEntity("param"+i, values[i]);
				}
				
			}
			            
            query.executeUpdate();
            //trans.commit();
			session.flush();
        } catch (HibernateException e) {
            e.printStackTrace();
        }finally{
        	session.clear();
        	session.close();
        }
		
	}
	
	/**
	 * Metodo que prepara uma query de busca em formato String 
     * @param param o campo que será comparado na busca
     * @param values os valores a serem comparados na busca
     * @param type o tipo da classe onde vai ser buscado os resultados
     * @return
     */
    private String prepareUpdate(Object[] attributes, Object[] newValues, Object[] params, Object[] values, Class type) {
    	
    	String query = "UPDATE " + type.getName() + " type1 ";

    	query +="SET type1." + attributes[0] +"=" + newValues[0] +" ";

        for (int i = 1; i < values.length; i++) {
            query +=", type1."+ attributes[i] + "=" +values[i] +" ";      
        }

        query +="where type1." + params[0] +" = " + values[0] +" ";

        for (int i = 1; i < values.length; i++) {
            query +=" and type1."+ params[i] + " = " +values[i] +" ";      
        }
        return query;
    }
	
}