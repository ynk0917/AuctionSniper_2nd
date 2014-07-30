package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
    void joinAuction(String itemId);

    public static class Item {
        public final String identifier;
        public final int stopPrice;

        public Item(String identifier, int stopPrice) {
            this.identifier = identifier;
            this.stopPrice = stopPrice;
        }

        @Override
        public boolean equals(Object rhs) {
            return EqualsBuilder.reflectionEquals(this, rhs);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return "Item: " + identifier + ", stop price: " + stopPrice;
        }
    }
}
