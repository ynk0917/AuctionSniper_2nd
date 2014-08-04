package auctionsniper.xmpp;

public interface XMPPFailureReporter {
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
