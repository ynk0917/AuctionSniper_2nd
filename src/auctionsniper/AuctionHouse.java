package auctionsniper;

public interface AuctionHouse {
    Auction auctionFor(UserRequestListener.Item item);
}
