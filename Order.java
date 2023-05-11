public class Order {
    private final int quantity;

    private static Long idCounter = 0L;

    private final Long id;

    private final Long placedAtTimestamp;

    private final Double price;

    private final boolean isBuy;

    private final String destination;

    public Order(Long placedAtTimestamp, Double price, Integer quantity, boolean isBuy, String destination) {
        this.id = idCounter;
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

    public Long getId(){
        return id;
    }

    public String getDestination(){
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
        return String.format("%s@%s", quantity, price);
    }


}