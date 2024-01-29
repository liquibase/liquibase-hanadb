package liquibase.ext.hana.snapshot;

import liquibase.database.Database;
import liquibase.snapshot.CachedRow;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.ColumnSnapshotGenerator;
import liquibase.statement.DatabaseFunction;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;

public class ColumnSnapshotGeneratorHana extends ColumnSnapshotGenerator {

    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        return PRIORITY_DATABASE;
    }

    @Override
    public Class<? extends SnapshotGenerator>[] replaces() {
        return new Class[]{ColumnSnapshotGenerator.class};
    }

    @Override
    protected Object readDefaultValue(CachedRow columnMetadataResultSet, Column columnInfo, Database database) {
        Object defaultValue = super.readDefaultValue(columnMetadataResultSet, columnInfo, database);
        String typeName = columnMetadataResultSet.getString("TYPE_NAME").toUpperCase();
        if ("BOOLEAN".equals(typeName)) {
            if (defaultValue instanceof DatabaseFunction) {
                String defaultAsString = ((DatabaseFunction) defaultValue).getValue();
                if ("0".equals(defaultAsString)) {
                    return "FALSE";
                } else if ("1".equals(defaultAsString)) {
                    return "TRUE";
                }
            }
        }
        return defaultValue;
    }
}
