package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {

    public void sniperStateChanged(SniperSnapshot sniperState);
}
