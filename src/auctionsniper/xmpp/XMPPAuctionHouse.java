package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.Main;

public class XMPPAuctionHouse implements AuctionHouse {

    private final XMPPConnection connection;
    
    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }
    @Override
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, itemId);
    }
    
    public static XMPPAuctionHouse connect(String hostname, String username, String password) {
        XMPPConnection connection = new XMPPConnection(hostname);
        try {
            connection.connect();
            connection.login(username, password, Main.AUCTION_RESOURCE);
            return new XMPPAuctionHouse(connection);
        } catch (XMPPException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void disconnect() {
        connection.disconnect();
    }

}
