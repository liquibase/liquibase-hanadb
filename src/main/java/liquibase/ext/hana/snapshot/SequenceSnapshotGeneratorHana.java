package liquibase.ext.hana.snapshot;

import liquibase.database.Database;
import liquibase.ext.hana.HanaDatabase;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.SequenceSnapshotGenerator;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Schema;

public class SequenceSnapshotGeneratorHana extends SequenceSnapshotGenerator {

    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (database instanceof HanaDatabase) {
            int priority = super.getPriority(objectType, database);
            return priority + PRIORITY_DATABASE;
        } else {
            return PRIORITY_NONE;
        }
    }

    @Override
    public Class<? extends SnapshotGenerator>[] replaces() {
        return new Class[] { SequenceSnapshotGenerator.class };
    }

    @Override
    protected SqlStatement getSelectSequenceStatement(Schema schema, Database database) {
        if (database instanceof HanaDatabase) {
            return new RawSqlStatement("SELECT SEQUENCE_NAME FROM SYS.SEQUENCES WHERE SCHEMA_NAME='" + schema.getName() + "' AND LEFT(SEQUENCE_NAME, 5) != '_SYS_'");
        }

        return super.getSelectSequenceStatement(schema, database);
    }

}
