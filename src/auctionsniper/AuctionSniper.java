package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private Auction auction;
    private SniperListener sniperListener;
    private boolean isWinning = false;
    private SniperSnapshot snapshot;
    
    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }
    @Override
    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }
    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);;
        }
        sniperListener.sniperStateChanged(snapshot);
        
    }
}
