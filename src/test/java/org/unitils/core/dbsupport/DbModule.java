package org.unitils.core.dbsupport;

import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;

import java.lang.reflect.Method;
import java.util.Properties;

import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.TestListener;
import org.unitils.database.DatabaseModule;
import org.unitils.database.annotations.Transactional;
import org.unitils.util.PropertyUtils;

public class DbModule extends DatabaseModule{
	public void init(Properties configuration) {
        super.init(configuration);
        disableConstraints();
    }
	
	public TestListener getTestListener() {
        return new DatabaseTestListener();
    }
	protected class DatabaseTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
        	updateSequences();
            injectDataSource(testObject);
            startTransactionForTestMethod(testObject, testMethod);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            endTransactionForTestMethod(testObject, testMethod);
        }
    }
}
