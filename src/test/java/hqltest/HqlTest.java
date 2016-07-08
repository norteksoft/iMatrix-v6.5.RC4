package hqltest;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.UserManager;
/**
 * 使用时修改 src/test/resources下的
 * application_testMysql.properties
 * application_testOracle.properties
 * application_testSqlserver.properties
 * 改成对应数据库的连接配置
 */
public class HqlTest {
	public static void main(String[] args){
		final String[][] strs={{"applicationContext-memcache.xml","applicationContext-security.xml","beans_mysql.xml"},
				{"applicationContext-memcache.xml","applicationContext-security.xml","beans_oracle.xml"},
				{"applicationContext-memcache.xml","applicationContext-security.xml","beans_sqlserver.xml"}};
		for(final String[] str:strs){
			new Thread(new Runnable() {
				public void run() {
					excuteHql(str);
				}
			}).start();
		}
	}
	public static void excuteHql(String[] str){
		ApplicationContext content=new ClassPathXmlApplicationContext(str);
		SessionFactory session=(SessionFactory)content.getBean("sessionFactory");
		UserManager userManager=(UserManager)content.getBean("userManager");
		List<User> users=userManager.getAllUser();
		System.out.println(users.size());
		session.openSession().createQuery("from Department").list();
	}
}
