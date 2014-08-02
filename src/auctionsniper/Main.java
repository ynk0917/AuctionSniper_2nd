package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;
    
    public Main() throws Exception {
        startUserInterface();
    }
    
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                ui = new MainWindow(portfolio);
            }
        });
    }
    
    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
                args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }
    
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) throws Exception {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse xmppAuctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosed(WindowEvent e) {
                xmppAuctionHouse.disconnect();
            }
        });
    }
    
    public static class SniperLauncher implements UserRequestListener {
        private final AuctionHouse auctionHouse;
        private final SniperCollector collector;

        public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
            this.auctionHouse = auctionHouse;
            this.collector = collector;
        }

        @Override
        public void joinAuction(Item item) {
            Auction auction = auctionHouse.auctionFor(item);
            AuctionSniper sniper = new AuctionSniper(item, auction);
            auction.addAuctionEventListener(sniper);
            collector.addSniper(sniper);
            auction.join();
        }
    }
}
