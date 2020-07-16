package liquibase.ext.hana.datatype;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.ClobType;
import liquibase.exception.DatabaseException;
import liquibase.ext.hana.HanaDatabase;
import liquibase.logging.LogService;
import liquibase.logging.Logger;

@DataTypeInfo(name = "clob", aliases = { "longvarchar", "java.sql.Types.LONGVARCHAR",
		"java.sql.Types.CLOB" }, minParameters = 0, maxParameters = 0, priority = LiquibaseDataType.PRIORITY_DATABASE)
public class ClobTypeHana extends ClobType {

	@Override
	public int getPriority() {
		return PRIORITY_DATABASE;
	}

	@Override
	public boolean supports(Database database) {
		return database instanceof HanaDatabase;
	}

	@Override
	public DatabaseDataType toDatabaseDataType(Database database) {
		try {
			if ( database.getDatabaseMajorVersion() >= 4 ) {
				return new DatabaseDataType( "NCLOB" );
			}
		}
		catch (DatabaseException e) {
			Scope.getCurrentScope().getLog(getClass()).warning( "Unable to determine the database version.", e );
		}
		return new DatabaseDataType( "CLOB" );
	}
}
