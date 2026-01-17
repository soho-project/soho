package work.soho.longlink.biz.connect;

import junit.framework.TestCase;

import java.util.HashSet;

public class ConnectManagerImplTest extends TestCase {

    public void testBindUidAndCleanup() {
        ConnectManagerImpl manager = new ConnectManagerImpl();

        manager.bindUid("c1", "u1");
        HashSet<String> set = manager.getConnectIdListByUid("u1");
        assertNotNull(set);
        assertTrue(set.contains("c1"));

        manager.removeConnectIdFromUid("c1", "u1");
        assertNull(manager.getConnectIdListByUid("u1"));
    }
}
