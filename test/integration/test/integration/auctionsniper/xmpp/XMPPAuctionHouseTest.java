package test.integration.auctionsniper.xmpp;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import auctionsniper.UserRequestListener;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class XMPPAuctionHouseTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item54321");
    private XMPPAuctionHouse auctionHouse;

    @Before
    public void openConnection() throws XMPPException {
        auctionHouse= XMPPAuctionHouse.connect(FakeAuctionServer.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    }
    
    @Before
    public void startAuction() throws XMPPException {
        auctionServer.startSellingItem();
    }
    
    @After
    public void closeConnection() {
        if (auctionHouse != null) {
            auctionHouse.disconnect();
        }
    }
    
    @After
    public void stopAuction() {
        auctionServer.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        
        Auction auction = auctionHouse.auctionFor(new UserRequestListener.Item(auctionServer.getItemId(), 576));
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
        
        auction.join();
        auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();
        
        assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
    }
    
    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionClosed) {
        return new AuctionEventListener() {
            
            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
            }
            
            @Override
            public void auctionClosed() {
                auctionClosed.countDown();
            }
        };
    }

}
