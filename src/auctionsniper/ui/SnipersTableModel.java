package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 6638492513334189284L;
    
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private static String[] STATUS_TEXT = { MainWindow.STATUS_JOINING, MainWindow.STATUS_BIDDING };
    private String state = MainWindow.STATUS_JOINING;
    private SniperSnapshot snapshot = STARTING_UP;

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return snapshot.itemId;
            case LAST_PRICE:
                return snapshot.lastPrice;
            case LAST_BID:
                return snapshot.lastBid;
            case SNIPER_STATE:
                return state;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }
    
    public void setStatusText(String newStatusText) {
        state = newStatusText;
        fireTableCellUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperSnapshot snapshot) {
        this.snapshot = snapshot;
        this.state = STATUS_TEXT[snapshot.state.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
}
