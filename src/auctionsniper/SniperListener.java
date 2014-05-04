package auctionsniper;

public interface SniperListener {

    public void sniperLost();
    public void sniperBidding(SniperState sniperState);
    public void sniperWinning();
    public void sniperWon();
}
