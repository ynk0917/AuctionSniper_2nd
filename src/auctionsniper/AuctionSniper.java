package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private Auction auction;
    private SniperSnapshot snapshot;
    private Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final UserRequestListener.Item item;
    
    public AuctionSniper(UserRequestListener.Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }
    
    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }
    
    @Override
    public void auctionClosed() {
        snapshot = snapshot.close();
        notifyChange();
    }
    
    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                if (item.allowBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                } else {
                    snapshot = snapshot.losing(price);
                }
                break;
        }
        notifyChange();
    }

    @Override
    public void auctionFailed() {

    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }
    
    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
}
