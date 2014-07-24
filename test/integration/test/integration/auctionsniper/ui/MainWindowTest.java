package test.integration.auctionsniper.ui;

import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import test.endtoend.auctionsniper.AuctionSniperDriver;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
    
    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("an item id"), "join request");
        
        mainWindow.addUserRequestListener(new UserRequestListener() {
            
            @Override
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });
        
        driver.startBiddingWithStopPrice("an item id", 789);
        driver.check(buttonProbe);
    }
}
