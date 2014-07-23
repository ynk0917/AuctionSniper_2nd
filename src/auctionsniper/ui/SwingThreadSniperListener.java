package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

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