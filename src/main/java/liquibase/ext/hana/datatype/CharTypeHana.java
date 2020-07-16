package liquibase.ext.hana.datatype;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.CharType;
import liquibase.exception.DatabaseException;
import liquibase.ext.hana.HanaDatabase;
import liquibase.logging.LogService;
import liquibase.logging.Logger;

@DataTypeInfo(name = "char", aliases = { "java.sql.Types.CHAR",
		"bpchar" }, minParameters = 0, maxParameters = 1, priority = LiquibaseDataType.PRIORITY_DATABASE)
public class CharTypeHana extends CharType {

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
		String typeString = "VARCHAR";
		try {
			if ( database.getDatabaseMajorVersion() >= 4 ) {
				typeString = "NVARCHAR";
			}
		}
		catch (DatabaseException e) {
			Scope.getCurrentScope().getLog(getClass()).warning( "Unable to determine the database version.", e );
		}

		Object[] parameters = getParameters();
		if ( parameters.length > 0 ) {
			return new DatabaseDataType( typeString, parameters[0] );
		}
		return new DatabaseDataType( typeString );
	}

}
