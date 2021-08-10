package liquibase.ext.hana.change;

import org.junit.*;

import liquibase.ext.hana.testing.ITBase;

public class AddForeignKeyChangeIT extends ITBase {
    @Before
    public void setUp() throws Exception {
        changeLogFile = "changelogs/AddForeignKeyChangelog/changelog.test.xml";
        connectToDB();
        cleanDB();
    }

    @Test
    public void test() throws Exception {
        if (connection == null) {
            return;
        }
        liquiBase.update((String) null);
    }
}
