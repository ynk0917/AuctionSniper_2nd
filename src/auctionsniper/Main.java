package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    
    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    
    public Main() throws Exception {
        startUserInterface();
    }
    
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                ui = new MainWindow(snipers);
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
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, snipers));
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse xmppAuctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosed(WindowEvent e) {
                xmppAuctionHouse.disconnect();
            }
        });
    }
    
    class SwingThreadSniperListener implements SniperListener {
        private SnipersTableModel snipers;
        
        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot snapshot) {
            snipers.sniperStatusChanged(snapshot);
        }
    }
    
    public static class SniperLauncher implements UserRequestListener {
        private final List<Auction> notToBeGCd = new ArrayList<Auction>();
        private final AuctionHouse auctionHouse;
        private final SniperCollector collector;

        public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
            this.auctionHouse = auctionHouse;
            this.collector = collector;
        }

        @Override
        public void joinAuction(String itemId) {
            Auction auction = auctionHouse.auctionFor(itemId);
            notToBeGCd.add(auction);
            AuctionSniper sniper = new AuctionSniper(itemId, auction);
            collector.addSniper(sniper);
            auction.join();
        }
    }
}
