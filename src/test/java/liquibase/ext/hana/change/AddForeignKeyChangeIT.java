package liquibase.ext.hana.change;

import liquibase.Liquibase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.*;

import liquibase.ext.hana.testing.ITBase;

public class AddForeignKeyChangeIT extends ITBase {
    @Before
    public void setUp() throws Exception {
        changeLogFile = "changelogs/AddForeignKeyChangelog/changelog.test.xml";
        connectToDB();
        liquiBase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), jdbcConnection);
        if (connection != null) {
            liquiBase.dropAll();
        }
    }

    @Test
    public void test() throws Exception {
        if (connection == null) {
            return;
        }
        liquiBase.update((String) null);
    }
}
