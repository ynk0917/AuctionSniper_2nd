package auctionsniper;

public interface SniperListener {

    public void sniperLost();
    public void sniperStateChanged(SniperSnapshot sniperState);
    public void sniperWon();
}
