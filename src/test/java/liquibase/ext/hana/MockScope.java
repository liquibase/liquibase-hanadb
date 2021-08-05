package liquibase.ext.hana;

import java.util.HashMap;
import java.util.Map;

import liquibase.Scope;
import liquibase.SingletonObject;
import liquibase.logging.core.JavaLogService;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.servicelocator.StandardServiceLocator;
import liquibase.ui.ConsoleUIService;

public class MockScope extends Scope {

	private static final Map<String, Object> DEFAULT_SCOPE_VALUES = getDefaultScopeValues();
	private Map<Class<?>, Object> singletons = new HashMap<>();

	private static Map<String, Object> getDefaultScopeValues() {
		Map<String, Object> scopeValues = new HashMap<>();
		scopeValues.put( Attr.logService.name(), new JavaLogService() );
		scopeValues.put( Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor() );
		scopeValues.put( Attr.serviceLocator.name(), new StandardServiceLocator() );
		scopeValues.put( Attr.ui.name(), new ConsoleUIService() );
		return scopeValues;
	}

	public MockScope() {
		this( null, DEFAULT_SCOPE_VALUES );
	}

	public MockScope(Scope parent, Map<String, Object> scopeValues) {
		super( parent, scopeValues );

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SingletonObject> T getSingleton(Class<T> type) {
		return (T) this.singletons.get( type );
	}

	public <T extends SingletonObject> void putSingleton(Class<T> type, T singleton) {
		this.singletons.put( type, singleton );
	}

}