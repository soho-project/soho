package work.soho.longlink.biz.connect;

import junit.framework.TestCase;
import work.soho.longlink.biz.metrics.LongLinkMetrics;

import java.util.HashSet;

import static org.mockito.Mockito.mock;

public class ConnectManagerImplTest extends TestCase {

    public void testBindUidAndCleanup() {
        LongLinkMetrics metrics = mock(LongLinkMetrics.class);
        ConnectManagerImpl manager = new ConnectManagerImpl(metrics);

        manager.bindUid("c1", "u1");
        HashSet<String> set = manager.getConnectIdListByUid("u1");
        assertNotNull(set);
        assertTrue(set.contains("c1"));

        manager.removeConnectIdFromUid("c1", "u1");
        assertNull(manager.getConnectIdListByUid("u1"));
    }
}
