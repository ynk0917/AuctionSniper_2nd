package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

@RunWith(JMock.class)
public class SniperTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }
    
    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }
    
    @Test
    public void setSniperValuesInColumns() {
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));;
            
            one(listener).tableChanged(with(aChangeInRow(0)));
        }});
        
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);
        model.addSniper(joining);
        model.sniperStatusChanged(bidding);
        
        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATE, MainWindow.STATUS_BIDDING);
    }
    
    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    } 
    
    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }
    
    @Test
    public void notifiesListenersWhenAddingSniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anyInsertionEvent()));
        }});
        
        
        assertEquals(0,  model.getRowCount());
        
        model.addSniper(joining);
        
        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }
    
    @Test
    public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));
        
        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }
    
    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
    
    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
    }
    
    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }
    
    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}
