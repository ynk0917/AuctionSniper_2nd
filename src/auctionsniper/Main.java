package auctionsniper;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_LOST = "Lost";
    
    private MainWindow ui;
    
    public Main() throws Exception {
        startUserInterface();
    }
    
    public static void main(String... args) throws Exception {
        new Main();
    }
    
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
