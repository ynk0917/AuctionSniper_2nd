package auctionsniper;

public interface SniperListener {

    public void sniperLost();
    public void sniperBidding(SniperSnapshot sniperState);
    public void sniperWinning();
    public void sniperWon();
}
