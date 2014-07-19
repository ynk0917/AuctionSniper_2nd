package auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 6638492513334189284L;
    
    private static String[] STATUS_TEXT = { 
        "Joining",
        "Bidding",
        "Winning",
        "Lost",
        "Won"
        };
    private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return Column.at(columnIndex).name;
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public void sniperStatusChanged(SniperSnapshot newSnapshot) {
        snapshots.set(0, newSnapshot);
        fireTableRowsUpdated(0, 0);
    }

    public void addSniper(SniperSnapshot joining) {
        snapshots.add(joining);
        fireTableRowsInserted(0, 0);
    }
}
