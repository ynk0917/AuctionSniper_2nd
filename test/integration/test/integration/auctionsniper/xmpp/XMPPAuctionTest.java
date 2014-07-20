package test.integration.auctionsniper.xmpp;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.XMPPAuction;

public class XMPPAuctionTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private XMPPConnection connection;

    @Before
    public void connect() throws XMPPException {
        auctionServer.startSellingItem();
        connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
        try {
            connection.connect();
            connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, FakeAuctionServer.AUCTION_RESOURCE);
        } catch (XMPPException xmppe) {
            Assert.fail();
        }
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        
        Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
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
