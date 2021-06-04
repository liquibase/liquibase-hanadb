package liquibase.ext.hana.change;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import liquibase.Scope;
import org.junit.Before;
import org.junit.Test;

import liquibase.change.Change;
import liquibase.change.ChangeFactory;
import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.core.DropColumnChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.hana.HanaDatabase;
import liquibase.ext.hana.testing.ITBase;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropColumnStatement;

public class DropColumnChangeIT extends ITBase {

	@Before
	public void setUp() throws Exception {
		changeLogFile = "changelogs/DropColumnChange/changelog.test.xml";
		connectToDB();
		cleanDB();
	}

	@Test
	public void test() throws Exception {
		if (connection == null) {
			return;
		}
		liquiBase.update((String) null);
	}

	@Test
	public void generateStatement() {
		DropColumnChange change = new DropColumnChange();

		change.setTableName("MY_TAB");
		change.setColumnName("C1");

		HanaDatabase database = new HanaDatabase();

		SqlStatement[] statements = change.generateStatements(database);
		assertEquals(1, statements.length);
		DropColumnStatement statement = (DropColumnStatement) statements[0];

		assertEquals(database.getDefaultSchemaName(), statement.getSchemaName());
		assertEquals("MY_TAB", statement.getTableName());
		assertEquals("C1", statement.getColumnName());
	}

	@Test
	public void generateStatementMultiple() {
		DropColumnChange change = new DropColumnChange();

		change.setTableName("MY_TAB");

		ColumnConfig c1 = new ColumnConfig();
		c1.setName("C1");
		c1.setType("INTEGER");

		change.addColumn(c1);

		ColumnConfig c2 = new ColumnConfig();
		c2.setName("C2");
		c2.setType("NVARCHAR(256)");

		change.addColumn(c2);

		HanaDatabase database = new HanaDatabase();

		SqlStatement[] statements = change.generateStatements(database);
		assertEquals(1, statements.length);
		DropColumnStatement statement = (DropColumnStatement) statements[0];

		assertEquals(database.getDefaultSchemaName(), statement.getSchemaName());
		assertNull(statement.getTableName());
		assertEquals(2, statement.getColumns().size());
		assertEquals("MY_TAB", statement.getColumns().get(0).getTableName());
		assertEquals("C1", statement.getColumns().get(0).getColumnName());
		assertEquals("MY_TAB", statement.getColumns().get(1).getTableName());
		assertEquals("C2", statement.getColumns().get(1).getColumnName());
	}

	@Test
	public void getConfirmationMessage() {
		DropColumnChange change = new DropColumnChange();

		change.setTableName("MY_TAB");
		change.setColumnName("C1");

		assertEquals("Column " + change.getTableName() + "." + change.getColumnName() + " dropped",
				change.getConfirmationMessage());
	}

	@Test
	public void getConfirmationMessageMultiple() {
		DropColumnChange change = new DropColumnChange();

		change.setTableName("MY_TAB");

		ColumnConfig c1 = new ColumnConfig();
		c1.setName("C1");
		c1.setType("INTEGER");

		change.addColumn(c1);

		ColumnConfig c2 = new ColumnConfig();
		c2.setName("C2");
		c2.setType("NVARCHAR(256)");

		change.addColumn(c2);

		assertEquals("Columns C1,C2 dropped from " + change.getTableName(), change.getConfirmationMessage());
	}

	@Test
	public void getChangeMetaData() {
		DropColumnChange change = new DropColumnChange();

		ChangeFactory changeFactory = Scope.getCurrentScope().getSingleton(ChangeFactory.class);

		assertEquals("dropColumn", changeFactory.getChangeMetaData(change).getName());
		assertEquals(ChangeMetaData.PRIORITY_DEFAULT, changeFactory.getChangeMetaData(change).getPriority());
	}

	@Test
	public void parseAndGenerate() throws Exception {
		if (connection == null) {
			return;
		}

		Database database = liquiBase.getDatabase();
		ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

		ChangeLogParameters changeLogParameters = new ChangeLogParameters();

		DatabaseChangeLog changeLog = ChangeLogParserFactory.getInstance().getParser(changeLogFile, resourceAccessor)
				.parse(changeLogFile, changeLogParameters, resourceAccessor);

		changeLog.validate(database);

		List<ChangeSet> changeSets = changeLog.getChangeSets();

		List<String> expectedQuery = new ArrayList<>();

		expectedQuery.add("ALTER TABLE LIQUIBASE_TEST.MY_TAB DROP (C1)");
		expectedQuery.add("ALTER TABLE LIQUIBASE_TEST.MY_TAB DROP (C2,C3)");

		int i = 0;
		for (ChangeSet changeSet : changeSets) {
			for (Change change : changeSet.getChanges()) {
				Sql[] sql = SqlGeneratorFactory.getInstance().generateSql(change.generateStatements(database)[0],
						database);
				if (i == 3) {
					assertEquals(expectedQuery.get(0), sql[0].toSql());
				} else if (i == 4) {
					assertEquals(expectedQuery.get(1), sql[0].toSql());
				}
			}
			i++;
		}
	}
}
