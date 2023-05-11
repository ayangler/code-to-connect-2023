public class Order {
    private final int quantity;

    private static Long idCounter = 0L;

    private final Long id;

    private final String orderId;

    private final Long placedAtTimestamp;

    private final Double price;

    private final boolean isBuy;

    private final String destination;

    private boolean isSOR;

    public Order(Long placedAtTimestamp, Double price, Integer quantity, boolean isBuy, String destination,
            String orderId) {
        id = idCounter;
        this.orderId = orderId;

        idCounter++;

        this.placedAtTimestamp = placedAtTimestamp;
        this.price = price;
        this.quantity = quantity;
        this.isBuy = isBuy;
        this.destination = destination;

    }

    public Long getPlacedAtTimestamp() {
        return placedAtTimestamp;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public boolean getIsBuy() {
        return isBuy;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getId() {
        return id;

    }

    public String getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order)) {
            return false;
        }

        Order otherOrder = (Order) o;

        return this.id.equals(otherOrder.id);
    }

    @Override
    public String toString() {
        String side = isBuy ? "Buy" : "Sell";
        return String.format("%d [%s][%s] %s %s@%s", placedAtTimestamp, orderId, destination, side, quantity, price);
    }

    public boolean getIsSOR() {
        return isSOR;
    }

    public void setIsSOR(boolean isSOR) {
        this.isSOR = isSOR;
    }



}