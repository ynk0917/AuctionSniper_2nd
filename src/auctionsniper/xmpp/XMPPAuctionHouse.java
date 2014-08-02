package auctionsniper.xmpp;

import auctionsniper.UserRequestListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;

public class XMPPAuctionHouse implements AuctionHouse {

    private final XMPPConnection connection;
    public static final String AUCTION_RESOURCE = "Auction";
    
    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }
    @Override
    public Auction auctionFor(UserRequestListener.Item item) {
        return new XMPPAuction(connection, item.identifier);
    }
    
    public static XMPPAuctionHouse connect(String hostname, String username, String password) {
        XMPPConnection connection = new XMPPConnection(hostname);
        try {
            connection.connect();
            connection.login(username, password, XMPPAuctionHouse.AUCTION_RESOURCE);
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
