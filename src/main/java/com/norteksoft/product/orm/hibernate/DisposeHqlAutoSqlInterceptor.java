package com.norteksoft.product.orm.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;

import com.norteksoft.product.util.ParameterUtils;
/**
 * 处理自动生成的sql语句，添加上自定义的条件
 * @author ldx
 *
 */
public class DisposeHqlAutoSqlInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	 public String onPrepareStatement(String sql)
	  {
		return packageSql(sql);
	  }
	
	private static String packageSql(String sql){
		String tempSql = sql.toLowerCase();
		String classMethodName = ParameterUtils.getClassMethodName();
		if(StringUtils.isNotEmpty(classMethodName)){
			if(!tempSql.contains("insert into ")){//表示不是insert语句时才需要添加自定义条件
				String customWhereCondition = "'"+classMethodName+"'='"+classMethodName+"'";
				if(tempSql.contains(customWhereCondition.toLowerCase())){//如果sql语句已经包含自定义条件，则不需要再做处理
					return sql;
				}
				if(!tempSql.contains(" where ")){
					String resultSql = null;
					String commonSql = sql;
					String orderBy = null;
					if(tempSql.contains("count(*)")){//count语句时
						if(tempSql.contains(" _default_table")){//当包含distinct时会有该字符串，在com.norteksoft.product.orm.hibernate.HibernateDao中的countSql中添加的该字符串
							commonSql = tempSql.substring(0,tempSql.lastIndexOf(")"));
							resultSql = commonSql+" where "+customWhereCondition+") _default_table";
						}else{
							resultSql = tempSql+" where "+customWhereCondition;
						}
					}else{//非count语句时
						if(tempSql.contains(" order by ")){//是否包含order by，如果包含则以order by截取sql语句为两部分commonSql和orderBy
							commonSql = tempSql.substring(0,tempSql.lastIndexOf(" order by "));
							orderBy = tempSql.substring(tempSql.lastIndexOf(" order by ")+10);
						}
						if(commonSql.contains(" limit ?")){//sql语句是否包含limit ?,包含则以limit ?截取commonSql为两部分
							commonSql = commonSql.substring(0,tempSql.lastIndexOf(" limit ?"));
							resultSql = commonSql +" where "+customWhereCondition+" limit ?";//添加自定义的条件
						}else{
							resultSql = commonSql+" where "+customWhereCondition;
						}
						if(StringUtils.isNotEmpty(orderBy)){
							resultSql = resultSql + " order by "+orderBy;
						}
					}
					return resultSql;
				}else{
					String beforeWhere = tempSql.substring(0,tempSql.indexOf(" where "));
					String whereCondition = tempSql.substring(tempSql.indexOf(" where ")+7);;
					sql = beforeWhere+" where "+customWhereCondition+" and "+whereCondition;
					return sql;
				}
			}
		}
		return sql;
	}
	
//	public static void main(String[] args) {
//		String sql = " select "
//        +"count(*)" 
//        +" from " 
//        +"cbm_order o where id=2";
//		ParameterUtils.setClassMethodName("/aaa/bb.htm");
//		System.out.println(packageSql(sql));
//	}
}
