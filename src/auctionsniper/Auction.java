package auctionsniper;

public interface Auction {

    public void join();
    public void bid(int i);
    public void addAuctionEventListener(AuctionEventListener listener);
}
