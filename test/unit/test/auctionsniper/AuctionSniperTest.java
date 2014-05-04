package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.SniperState;

@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item_id";
    
    private final Mockery context = new Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
    private final States sniperState = context.states("sniper");
    
    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperLost();
        }});
        
        sniper.auctionClosed();
    }
    
    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            one(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding(new SniperState(ITEM_ID, price, bid));
        }});
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});
        
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);
    }
    
    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperLost();
        }});
        sniper.auctionClosed();
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding();
                                    then(sniperState.is("bidding"));
                                    
            atLeast(1).of(sniperListener).sniperLost();
                                    when(sniperState.is("bidding"));
        }});
        
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
        
    }
    
    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning(); then(sniperState.is("winning"));
            
            atLeast(1).of(sniperListener).sniperWon(); when(sniperState.is("winning"));
        }});
        
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
}
