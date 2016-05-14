/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package br.ufpb.dicomflow.service;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;



public interface PersistentService {
	
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	
	public void saveOrUpdate(Object object) throws ServiceException;

	public Integer onlySave(Object object) throws ServiceException;

	public void remove(Object object) throws ServiceException;

	public void remove(String param, Integer value, Class type)
			throws ServiceException;
	
	public void removeByIds(String field, List<Integer> values, Class entity);
	
	public void removeByEntities(String field, List values, Class entity);
	
	public void remove(String field, Object value, Class entity);

	public List selectAll(Class type) throws ServiceException;

	public List selectAll(String param, Object value, Class type);

	public Object select(String param, Object value, Class type);

	public List selectAll(String param, List values, Class type);

	public List selectAll(String param, Object value, Class type, int maxResult);
	
	public List selectAllByParams(Object[] params, Object[] values, Class type);
	
	public Object selectByParams(Object[] params, Object[] values, Class type);

	public List selectLike(String[] param, Object value, Class type)
			throws Exception;

	public List selectLike(String[] param, Object[] value, Class type) throws Exception;
	
	public Integer selectCount(Class object);
	
	public Long selectCount(String field ,Object value, Class table);
	
	public List selectByDate(String param, Date value, int sign ,Class type ) throws HibernateException;
	
	public List selectByDates(String param, Object value, String dateParam, Date start, Date finish ,Class type, String joinCollection ) throws HibernateException;
	
	public List selectAllOrderBy(Class type,String orderType, String order);
	
	public List selectOrderBy(String param, Object value, Class type,String orderType, String order);
	
	public List selectOrderByWithParams(String param, List values, Class type,String orderType, String order);
	
	public List selectAllObjectsByJoinTable(String field, Object value, Class joinTable, String result);
	
	public List selectAllObjectsByJoinTable(String field, List value,String field2, List value2, Class joinTable);
	
	public List selectAllWithInnerSelect(String field, Class type,String InnerField, Object innerValue, Class innerType);
	
	public List selectPaging(int first, int max, Class type) throws Exception;
	
	public List selectPaging(String param, Object value, int first, int max, Class type) throws Exception;
	
	public List selectPagingOrderBy(int first, int max, Class type, String orderType, String order) throws Exception;
	
	public List selectPagingOrderBy(int first, int max, String param, Object value, Class type,String orderType, String order);
	
	public List selectPagingWithParams(Object[] params, Object[] values, int first, int max, Class type) throws Exception;
	
	public List selectAllFullJoin(String param, Object value, Class type, String fetch, String fetch2);
	
	public List selectAllInnerJoin(String param, Object value, Class type, String fetch);
	
	public List selectAllInnerJoin(Class type, String fetch);
	
	public Object selectInnerJoin(String param, Object value, Class type, String fetch);

	public void updatePrioridadeSessao(String[] fields, Object[] NewValues, String[] params, Object[] values,Class classBean);
	
	public Object selectObjectLoadLazyCollection(String param, Object value, Class type, String fetch);
	public Object selectObjectLoadLazyCollection(Class type, String fetch);
	
	public List selectUniqueResultOrderBy(String param, Object value, Class type,String orderType, String order);
	
	public List selectAllNotIn(String param, List values, Class type);
}
