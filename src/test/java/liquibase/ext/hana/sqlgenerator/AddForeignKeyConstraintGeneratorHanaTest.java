package liquibase.ext.hana.sqlgenerator;

import liquibase.change.ColumnConfig;
import liquibase.ext.hana.HanaDatabase;
import liquibase.sql.Sql;
import liquibase.statement.core.AddForeignKeyConstraintStatement;
import liquibase.structure.core.Column;
import org.junit.*;

import static org.junit.Assert.*;

public class AddForeignKeyConstraintGeneratorHanaTest {
    private HanaDatabase database = new HanaDatabase();
    private AddForeignKeyConstraintGeneratorHana generator = new AddForeignKeyConstraintGeneratorHana();
    private ColumnConfig baseColumn = new ColumnConfig(new Column("baseid"));
    private ColumnConfig refColumn = new ColumnConfig(new Column("referencekey"));

    @Test
    public void testAddForeignKeyNoCascade() {
        AddForeignKeyConstraintStatement statement = new AddForeignKeyConstraintStatement("fk", null,
                null, "basetable", new ColumnConfig[]{baseColumn}, null, null,
                "referencetable", new ColumnConfig[]{refColumn});
        final Sql[] sql = generator.generateSql(statement, database, null);
        assertEquals(1, sql.length);
        assertEquals("ALTER TABLE basetable ADD CONSTRAINT fk FOREIGN KEY (baseid) REFERENCES referencetable (referencekey)", sql[0].toSql());
    }
    @Test
    public void testAddForeignKeyCascadeNoAction() {
        AddForeignKeyConstraintStatement statement = new AddForeignKeyConstraintStatement("fk", null,
                null, "basetable", new ColumnConfig[]{baseColumn}, null, null,
                "referencetable", new ColumnConfig[]{refColumn});
        statement.setOnDelete("no action");
        statement.setOnUpdate("no action");
        final Sql[] sql = generator.generateSql(statement, database, null);
        assertEquals(1, sql.length);
        assertEquals("ALTER TABLE basetable ADD CONSTRAINT fk FOREIGN KEY (baseid) REFERENCES referencetable (referencekey)", sql[0].toSql());
    }
    @Test
    public void testAddForeignKeyCascadeDeleteAndUpdate() {
        AddForeignKeyConstraintStatement statement = new AddForeignKeyConstraintStatement("fk", null,
                null, "basetable", new ColumnConfig[]{baseColumn}, null, null,
                "referencetable", new ColumnConfig[]{refColumn});
        statement.setOnDelete("cascade");
        statement.setOnUpdate("set null");
        final Sql[] sql = generator.generateSql(statement, database, null);
        assertEquals(1, sql.length);
        assertEquals("ALTER TABLE basetable ADD CONSTRAINT fk FOREIGN KEY (baseid) REFERENCES referencetable (referencekey) ON UPDATE set null ON DELETE cascade", sql[0].toSql());
    }
}
