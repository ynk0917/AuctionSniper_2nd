package auctionsniper.xmpp;

import java.util.logging.Logger;

import static java.lang.String.format;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    private static final String MESSAGE_FORMAT = "<%s> Could not translate message \"%s\" because \"%s\"";

    private Logger logger;
    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(format(MESSAGE_FORMAT, auctionId, failedMessage, exception.toString()));
    }
}
