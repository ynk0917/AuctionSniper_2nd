package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Main.SniperLauncher;

@RunWith(JMock.class)
public class SniperLauncherTest {
    private final Mockery context = new Mockery();
    private States auctionState = context.states("auction state").startsAs("not joined");
    private Auction auction = context.mock(Auction.class);
    private AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private SniperCollector collector = context.mock(SniperCollector.class);
    private SniperLauncher launcher = new SniperLauncher(auctionHouse, collector);
    

    @Test
    public void addNewSniperToCollectionAndThenJoinAuction() {
        final UserRequestListener.Item item = new UserRequestListener.Item("item 123", 456);
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(item); will(returnValue(auction));
            
            oneOf(auction).addAuctionEventListener(with(sniperForItem(item))); when(auctionState.is("not joined"));
            
            oneOf(collector).addSniper(with(sniperForItem(item))); when(auctionState.is("not joined"));
            
            one(auction).join(); then(auctionState.is("joined"));
        }});
        
        launcher.joinAuction(item);
    }
    
    private Matcher<AuctionSniper>sniperForItem(UserRequestListener.Item item) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "sniper with item id", "item") {
            @Override protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }

}
