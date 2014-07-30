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
        final ValueMatcherProbe<UserRequestListener.Item> itemProbe = new ValueMatcherProbe<UserRequestListener.Item>(equalTo(new UserRequestListener.Item("an item id", 789)), "join request");
        
        mainWindow.addUserRequestListener(new UserRequestListener() {
            
            @Override
            public void joinAuction(Item item) {
                itemProbe.setReceivedValue(item);
            }
        });
        
        driver.startBiddingWithStopPrice("an item id", 789);
        driver.check(itemProbe);
    }
}
