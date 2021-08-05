package liquibase.ext.hana;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import liquibase.CatalogAndSchema;
import liquibase.Scope;
import liquibase.ScopeManager;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.ValidationErrors;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;

public class HanaDatabaseTest { // } extends AbstractJdbcDatabaseTest {

	@Test
	public void testNothing() {
		// placeholder test until we have access to the reusable integration tests
	}

	// public HanaDatabaseTest() throws Exception {
	// super(new HanaDatabase());
	// }
	//
	// @Test
	// public void getShortName() {
	// assertEquals("hana", new HanaDatabase().getShortName());
	// }
	//
	// @Override
	// protected String getProductNameString() {
	// return HanaDatabase.PRODUCT_NAME;
	// }
	//
	// @Override
	// public void supportsInitiallyDeferrableColumns() {
	// assertFalse(getDatabase().supportsInitiallyDeferrableColumns());
	// }
	//
	// @Override
	// public void getCurrentDateTimeFunction() {
	// assertEquals("CURRENT_TIMESTAMP", getDatabase().getCurrentDateTimeFunction());
	// }

	@Test
	public void getViewDefinition() throws DatabaseException {
		HanaDatabase hana = new HanaDatabase();
		Scope.setScopeManager( new ScopeManager() {

			private MockScope scope = new MockScope();

			@Override
			protected void setCurrentScope(Scope scope) {
				// TODO Auto-generated method stub
			}

			@Override
			protected Scope init(Scope scope) throws Exception {
				Constructor<ExecutorService> constructor = ExecutorService.class.getDeclaredConstructor();
				constructor.setAccessible( true );
				ExecutorService executorService = constructor.newInstance();
				executorService.setExecutor( "jdbc", hana, new Executor() {

					private int queryCounter = 0;

					@Override
					public ValidationErrors validate(ChangeSet changeSet) {
						return null;
					}

					@Override
					public boolean updatesDatabase() {
						return false;
					}

					@Override
					public int update(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return 0;
					}

					@Override
					public int update(SqlStatement sql) throws DatabaseException {
						return 0;
					}

					@Override
					public void setResourceAccessor(ResourceAccessor resourceAccessor) {

					}

					@Override
					public void setDatabase(Database database) {

					}

					@Override
					public <T> T queryForObject(SqlStatement sql, Class<T> requiredType, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return null;
					}

					@SuppressWarnings("unchecked")
					@Override
					public <T> T queryForObject(SqlStatement sql, Class<T> requiredType) throws DatabaseException {
						if ( this.queryCounter == 0 ) {
							this.queryCounter++;
							return (T) "CREATE MY_VIEW AS SELECT * FROM MY_TABLE";
						}
						return null;
					}

					@Override
					public long queryForLong(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return 0;
					}

					@Override
					public long queryForLong(SqlStatement sql) throws DatabaseException {
						return 0;
					}

					@Override
					public List queryForList(SqlStatement sql, Class elementType, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return null;
					}

					@Override
					public List<Map<String, ?>> queryForList(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return null;
					}

					@Override
					public List queryForList(SqlStatement sql, Class elementType) throws DatabaseException {
						return null;
					}

					@Override
					public List<Map<String, ?>> queryForList(SqlStatement sql) throws DatabaseException {
						return null;
					}

					@Override
					public int queryForInt(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
						return 0;
					}

					@Override
					public int queryForInt(SqlStatement sql) throws DatabaseException {
						return 0;
					}

					@Override
					public void modifyChangeSet(ChangeSet changeSet) {

					}

					@Override
					public int getPriority() {
						return 0;
					}

					@Override
					public String getName() {
						return null;
					}

					@Override
					public void execute(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {

					}

					@Override
					public void execute(Change change, List<SqlVisitor> sqlVisitors) throws DatabaseException {

					}

					@Override
					public void execute(SqlStatement sql) throws DatabaseException {

					}

					@Override
					public void execute(Change change) throws DatabaseException {

					}

					@Override
					public void comment(String message) throws DatabaseException {

					}
				} );
				this.scope.putSingleton( ExecutorService.class, executorService );
				this.scope.putSingleton( LiquibaseConfiguration.class, new LiquibaseConfiguration() {
					
				});
				return this.scope;
			}

			@Override
			public Scope getCurrentScope() {
				return this.scope;
			}
		} );
		String viewDefinition = hana.getViewDefinition( new CatalogAndSchema( null, "MY_SCHEMA" ), "MY_VIEW" );
		assertEquals( viewDefinition, "SELECT * FROM MY_TABLE" );
		viewDefinition = hana.getViewDefinition( new CatalogAndSchema( null, "MY_SCHEMA" ), "MY_VIEW" );
		assertEquals( viewDefinition, "[CANNOT READ VIEW DEFINITION]" );
	}
}
