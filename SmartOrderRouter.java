import java.util.List;

public class SmartOrderRouter {
    private SimulatedExchangeLit s;
    private SimulatedExchangeDark d;
    // private List<Order> filledBuyOrders;
    // private List<Order> filledSellOrders;
    public SmartOrderRouter(SimulatedExchangeLit s, SimulatedExchangeDark d) {
        // double splitRatio = calculateSplitRatio(order.getPrice(), fillRate, getOrderBookLiquidity());
        this.s = s;
        this.d = d;

    }
    public void routeOrder(Order order) {
       
        // Determine whether to send the order to the dark pool or the lit exchange
        //If the desired order price is within the price range of the lit exchange order book, then send the order to the lit exchange
        // order quantity is more than the current 
        if (order.getPrice() > s.getSellOrders().peek().getPrice()) {
            if(order.getQuantity() < s.getSellOrders().peek().getQuantity()){
                System.out.println("Route called");
                sendLitOrder(order);
            } else {
                // split the order
                // check if the quantity is a multiple of 100. if not, it cannot be split
                boolean canSplit = true;
                if (order.getQuantity() % 100 != 0){
                    canSplit = false;
                }
                if (canSplit){
                    int remainingQuantity = order.getQuantity() - s.getSellOrders().peek().getQuantity();
                    sendDarkOrder(createOrder(order, remainingQuantity));
                } else{
                    sendDarkOrder(order);
                } 
            }
            sendDarkOrder(order);
        } else if (order.getPrice() < s.getSellOrders().peek().getPrice()) {
            sendDarkOrder(order);
        } else {
            sendLitOrder(order);
            
        }
    }

    public Order createOrder(Order oldOrder, Integer newQuantity){
        return new Order(oldOrder.getPlacedAtTimestamp(),oldOrder.getPrice(),newQuantity,oldOrder.getIsBuy(),oldOrder.getDestination(),oldOrder.getDestination());
    }
        
    // }
    // private double calculateSplitRatio(double price, double fillRate, double orderBookLiquidity) {
    //     double desiredFillQuantity = orderBookLiquidity * fillRate;
    //     double litExchangeLiquidity = getLitExchangeLiquidityAtPrice(price);
    //     double darkPoolLiquidity = orderBookLiquidity - litExchangeLiquidity;
    //     double litExchangeFillRatio = Math.min(desiredFillQuantity / litExchangeLiquidity, 1);
    //     double darkPoolFillRatio = Math.min((desiredFillQuantity - litExchangeLiquidity) / darkPoolLiquidity, 1);
    //     double splitRatio = litExchangeFillRatio + darkPoolFillRatio;
    //     return splitRatio > 0 ? litExchangeFillRatio / splitRatio : DEFAULT_SPLIT_RATIO;
    // }

    private void sendDarkOrder(Order order){
        d.updatePQ(order);
    }
    private void sendLitOrder(Order order){
        s.updatePQ(order);
    }
    public void updateOrders(Order order){
        routeOrder(order);
    }

}
