package liquibase.ext.hana.change;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import liquibase.Scope;
import liquibase.change.Change;
import liquibase.change.ChangeFactory;
import liquibase.change.ChangeMetaData;
import liquibase.change.core.DropDefaultValueChange;
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
import liquibase.statement.core.DropDefaultValueStatement;

public class DropDefaultValueChangeIT extends ITBase {

	@Before
	public void setUp() throws Exception {
		changeLogFile = "changelogs/DropDefaultValueChange/changelog.test.xml";
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
		DropDefaultValueChange change = new DropDefaultValueChange();

		change.setTableName("MY_TAB");
		change.setColumnName("C1");
		change.setColumnDataType("INT(10,0)");

		HanaDatabase database = new HanaDatabase();

		SqlStatement[] statements = change.generateStatements(database);
		assertEquals(1, statements.length);
		DropDefaultValueStatement statement = (DropDefaultValueStatement) statements[0];

		assertEquals(database.getDefaultSchemaName(), statement.getSchemaName());
		assertEquals("MY_TAB", statement.getTableName());
		assertEquals("C1", statement.getColumnName());
		assertEquals("INT(10,0)", statement.getColumnDataType());
	}

	@Test
	public void getConfirmationMessage() {
		DropDefaultValueChange change = new DropDefaultValueChange();

		change.setTableName("MY_TAB");
		change.setColumnName("C1");

		assertEquals("Default value dropped from " + change.getTableName() + "." + change.getColumnName(),
				change.getConfirmationMessage());
	}

	@Test
	public void getChangeMetaData() {
		DropDefaultValueChange change = new DropDefaultValueChange();

		ChangeFactory changeFactory = Scope.getCurrentScope().getSingleton(ChangeFactory.class);

		assertEquals("dropDefaultValue", changeFactory.getChangeMetaData(change).getName());
		assertEquals("Removes the database default value for a column",
				changeFactory.getChangeMetaData(change).getDescription());
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

		expectedQuery.add("ALTER TABLE LIQUIBASE_TEST.MY_TAB ALTER (C1 INTEGER DEFAULT NULL)");

		int i = 0;
		for (ChangeSet changeSet : changeSets) {
			for (Change change : changeSet.getChanges()) {
				Sql[] sql = SqlGeneratorFactory.getInstance().generateSql(change.generateStatements(database)[0],
						database);
				if (i == 3) {
					assertEquals(expectedQuery.get(0), sql[0].toSql());
				}
			}
			i++;
		}
	}
}
