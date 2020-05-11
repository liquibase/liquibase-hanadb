package liquibase.ext.hana.datatype;

import liquibase.database.Database;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.VarcharType;
import liquibase.exception.DatabaseException;
import liquibase.ext.hana.HanaDatabase;
import liquibase.logging.LogService;
import liquibase.logging.Logger;

@DataTypeInfo(name = "varchar", aliases = { "java.sql.Types.VARCHAR", "java.lang.String", "varchar2",
		"character varying" }, minParameters = 0, maxParameters = 1, priority = LiquibaseDataType.PRIORITY_DATABASE)
public class VarcharTypeHana extends VarcharType {

	private static final Logger LOG = LogService.getLog( VarcharTypeHana.class );

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
			LOG.warning( "Unable to determine the database version.", e );
		}

		Object[] parameters = getParameters();
		if ( parameters.length > 0 ) {
			return new DatabaseDataType( typeString, parameters[0] );
		}
		return new DatabaseDataType( typeString );
	}

}
