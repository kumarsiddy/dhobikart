package freakydevelopers.dhobikart.pojo;

import java.io.Serializable;

/**
 * Created by PURUSHOTAM on 8/18/2017.
 */

public class OrderSummary implements Serializable {
    private String orderId;
    private int totalPrice;
    private String time;
    private int addressId;
    private boolean delivered;
    private boolean cancelled;

    public OrderSummary(String orderId, int totalPrice, String time, boolean delivered) {

        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.time = time;
        this.delivered = delivered;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
