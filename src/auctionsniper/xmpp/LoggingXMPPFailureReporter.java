package auctionsniper.xmpp;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    public LoggingXMPPFailureReporter(Logger logger) {
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
    }
}
