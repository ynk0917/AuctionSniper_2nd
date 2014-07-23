package auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


import auctionsniper.AuctionSniper;
import auctionsniper.Column;
import auctionsniper.SniperCollector;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperCollector {
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
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }
    
    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); ++i) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshots);
    }

    public void addSniper(SniperSnapshot joining) {
        snapshots.add(joining);
        fireTableRowsInserted(0, 0);
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        // TODO Auto-generated method stub
        
    }
}
