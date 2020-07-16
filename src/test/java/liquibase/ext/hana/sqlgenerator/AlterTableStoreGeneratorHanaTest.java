package liquibase.ext.hana.sqlgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import liquibase.datatype.DataTypeFactory;
import liquibase.ext.hana.HanaDatabase;
import liquibase.ext.hana.datatype.IntTypeHana;
import liquibase.ext.hana.statement.AlterTableStoreStatement;
import liquibase.sql.Sql;

public class AlterTableStoreGeneratorHanaTest {
	AlterTableStoreGenerator generator = new AlterTableStoreGenerator();
	HanaDatabase database = new HanaDatabase();

	@Test
	public void testColumn() {
		final AlterTableStoreStatement statement = new AlterTableStoreStatement("", "TABLE", "COLUMN");
		assertTrue(generator.supports(statement, database));
		assertFalse(generator.validate(statement, database, null).hasErrors());
		final Sql[] sql = generator.generateSql(statement, database, null);
		assertEquals(1, sql.length);
		assertEquals("ALTER TABLE TABLE COLUMN", sql[0].toSql());
	}

	@Test
	public void testRow() {
		final AlterTableStoreStatement statement = new AlterTableStoreStatement("", "TABLE", "ROW");
		assertTrue(generator.supports(statement, database));
		assertFalse(generator.validate(statement, database, null).hasErrors());
		final Sql[] sql = generator.generateSql(statement, database, null);
		assertEquals(1, sql.length);
		assertEquals("ALTER TABLE TABLE ROW", sql[0].toSql());
	}

	@Test
	public void testInvalid() {
		final AlterTableStoreStatement statement = new AlterTableStoreStatement("", "TABLE", "NOSTORE");
		assertTrue(generator.supports(statement, database));
		assertTrue(generator.validate(statement, database, null).hasErrors());
	}

}
