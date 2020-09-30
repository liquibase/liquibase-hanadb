package liquibase.ext.hana.sqlgenerator;

import liquibase.database.Database;
import liquibase.ext.hana.HanaDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AddForeignKeyConstraintGenerator;
import liquibase.statement.core.AddForeignKeyConstraintStatement;

/**
 * SQL generator for "add foreign key constraint" statements. This generator allows to have NO ACTION in the
 * onUpdate and onDelete properties
 *
 * @author gbandsmith
 */
public class AddForeignKeyConstraintGeneratorHana extends AddForeignKeyConstraintGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public boolean supports(AddForeignKeyConstraintStatement statement, Database database) {
        return database instanceof HanaDatabase;
    }

    @Override
    public Sql[] generateSql(AddForeignKeyConstraintStatement statement, Database database,
            SqlGeneratorChain sqlGeneratorChain) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
                .append(database.escapeTableName(statement.getBaseTableCatalogName(),
                        statement.getBaseTableSchemaName(), statement.getBaseTableName()))
                .append(" ADD CONSTRAINT ").append(database.escapeConstraintName(statement.getConstraintName()))
                .append(" FOREIGN KEY (").append(database.escapeColumnNameList(statement.getBaseColumnNames()))
                .append(") REFERENCES ")
                .append(database.escapeTableName(statement.getReferencedTableCatalogName(),
                        statement.getReferencedTableSchemaName(), statement.getReferencedTableName()))
                .append(" (").append(database.escapeColumnNameList(statement.getReferencedColumnNames())).append(")");
        // HANA does not have ON UPDATE NO ACTION statement
        if (statement.getOnUpdate() != null && !"NO ACTION".equalsIgnoreCase(statement.getOnUpdate())) {
            sb.append(" ON UPDATE ").append(statement.getOnUpdate());
        }
        // HANA does not have ON DELETE NO ACTION statement
        if (statement.getOnDelete() != null && !"NO ACTION".equalsIgnoreCase(statement.getOnDelete())) {
            sb.append(" ON DELETE ").append(statement.getOnDelete());
        }

        if (statement.isInitiallyDeferred()) {
            sb.append(" INITIALLY DEFERRED");
        }

        return new Sql[] { new UnparsedSql(sb.toString(), getAffectedForeignKey(statement)) };
    }
}