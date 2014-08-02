package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionEventListener.PriceSource;

@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item_id";
    private static UserRequestListener.Item ITEM = new UserRequestListener.Item(ITEM_ID, 1234);
    
    private final Mockery context = new Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);
    private final States sniperState = context.states("sniper");
    
    @Before
    public void attachListener() {
        sniper.addSniperListener(sniperListener);
    }
    
    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
        }});
        
        sniper.auctionClosed();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        allowingSniperLosing();
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1230, 0, SniperState.LOST));
            when(sniperState.is("losing"));
        }});

        sniper.currentPrice(1230, 456, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    private void allowingSniperLosing() {
        allowSniperStateChange(SniperState.LOSING, "losing");
    }

    private void allowSniperStateChange(final SniperState newState, final String oldState) {
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(newState))); then(sniperState.is(oldState));
        }});
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Sequence states = context.sequence("sniper states");
        final int price1 = 1233;
        final int price2 = 1258;

        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price1, 0, SniperState.LOSING)); inSequence(states);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price2, 0, SniperState.LOSING)); inSequence(states);
        }});

        sniper.currentPrice(price1, 25, PriceSource.FromOtherBidder);
        sniper.currentPrice(price2, 25, PriceSource.FromOtherBidder);
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            one(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                                    then(sniperState.is("bidding"));
                                    
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
                                    when(sniperState.is("bidding"));
        }});
        
        sniper.currentPrice(123, 12, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, AuctionEventListener.PriceSource.FromSniper);
    }
    
    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
        }});
        sniper.auctionClosed();
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                                    then(sniperState.is("bidding"));
                                    
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
                                    when(sniperState.is("bidding"));
        }});
        
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
        
    }
    
    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING))); then(sniperState.is("winning"));
            
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON))); when(sniperState.is("winning"));
        }});
        
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
    
    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "siper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
            when(sniperState.is("bidding"));
        }});
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                        then(sniperState.is("bidding"));
        }});
    }

    @Test public void
    doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;

        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, 0, SniperState.LOSING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;

        allowingSniperBidding();
        allowingSniperWinning();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.LOSING)); when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, PriceSource.FromSniper);
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    private void allowingSniperWinning() {
        allowSniperStateChange(SniperState.WINNING, "winning");
    }
}
