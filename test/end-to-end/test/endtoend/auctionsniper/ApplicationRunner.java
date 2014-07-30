package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static auctionsniper.ui.SnipersTableModel.textFor;


public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@localhost/Auction";
    
    private AuctionSniperDriver driver;
    
    public void startBiddingIn(final FakeAuctionServer ...auctions) {
        startSniper(auctions);
        for (FakeAuctionServer auction : auctions) {
            openBidding(auction, Integer.MAX_VALUE);
        }
    }

    private void openBidding(FakeAuctionServer auction, int stopPrice) {
        driver.startBiddingWithStopPrice(auction.getItemId(), stopPrice);
        driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
    }

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startSniper(auction);
        openBidding(auction, stopPrice);
    }

    private void startSniper(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test Application") {

            @Override
            public void run() {
                try {
                    Main.main(arguments(auctions));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }
    
    protected static String[] arguments(FakeAuctionServer... auctions) {
        String [] arguments = new String[auctions.length + 3];
        arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        
        for (int i = 0; i < auctions.length; ++i) {
            arguments[i + 3] = auctions[i].getItemId();
        }
        return arguments;
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
    }
    
    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
    }

    public void showsSniperStatusHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
    }

    public void showsSniperStatusHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
    }
}
