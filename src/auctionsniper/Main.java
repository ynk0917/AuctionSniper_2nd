package auctionsniper;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;

public class Main {
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
