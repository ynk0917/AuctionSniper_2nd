package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 6638492513334189284L;
    
    private String statusText = MainWindow.STATUS_JOINING;

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return statusText;
    }
    
    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableCellUpdated(0, 0);
    }
}
