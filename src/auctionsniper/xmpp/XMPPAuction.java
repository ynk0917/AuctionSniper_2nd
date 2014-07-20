package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Event: Bid; Price: %d";
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Event: JOIN;";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = XMPPAuction.ITEM_ID_AS_LOGIN + "@%s/" + XMPPAuctionHouse.AUCTION_RESOURCE;
    
    public XMPPAuction(XMPPConnection connection, String itemId) {
        chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
    }

    @Override
    public void join() {
        sendMessage(XMPPAuction.JOIN_COMMAND_FORMAT);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(XMPPAuction.BID_COMMAND_FORMAT, amount));
    }
    
    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(XMPPAuction.AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }
}
