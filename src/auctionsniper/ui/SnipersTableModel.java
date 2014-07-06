package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;

public class SnipersTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 6638492513334189284L;
    
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);
    private String statusText = MainWindow.STATUS_JOINING;
    private SniperSnapshot sniperState = STARTING_UP;

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
                return sniperState.itemId;
            case LAST_PRICE:
                return sniperState.lastPrice;
            case LAST_BID:
                return sniperState.lastBid;
            case SNIPER_STATE:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }
    
    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableCellUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperSnapshot sniperState, String newStatusText) {
        this.sniperState = sniperState;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
