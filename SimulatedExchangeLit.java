import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SimulatedExchangeLit {

    public final Comparator<Order> OrderComparatorBuy = new Comparator<Order>() {
        @Override
        public int compare(Order order1, Order order2) {
            // Ascending order
            if (order1.getPrice() < order2.getPrice()) {
                return 1;
            } else if (order1.getPrice() > order2.getPrice()) {
                return -1;
            } else {
                return order1.getPlacedAtTimestamp().compareTo(order2.getPlacedAtTimestamp());
            }
        }
    };

    public final Comparator<Order> OrderComparatorSell = new Comparator<Order>() {
        @Override
        public int compare(Order order1, Order order2) {
            // Descending order
            if (order1.getPrice() < order2.getPrice()) {
                return -1;
            } else if (order1.getPrice() > order2.getPrice()) {
                return 1;
            } else {
                return order1.getPlacedAtTimestamp().compareTo(order2.getPlacedAtTimestamp());
            }
        }
    };

    private PriorityQueue<Order> buyOrders = new PriorityQueue<Order>(OrderComparatorBuy);
    private PriorityQueue<Order> sellOrders = new PriorityQueue<Order>(OrderComparatorSell);

    public SimulatedExchangeLit(Order order) {
        if (order.getIsBuy() == true) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    public SimulatedExchangeLit() {
    }

    public PriorityQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }

    public CompletableFuture<Void> updatePQ(Order order) {
        CompletableFuture<Void> futureResult = CompletableFuture.runAsync(() -> {
            if (order.getIsBuy()) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }
        });
    
        return futureResult;
    }

    public CompletableFuture<Void> cancelOrder(Order order) {
        return CompletableFuture.runAsync(() -> {
            if (order.getIsBuy() == true) {
                buyOrders.remove(order);
            } else {
                sellOrders.remove(order);
            }
        });
    }

    public CompletableFuture<ArrayList<Order>> fulfillOrdersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Order> ordersFulfilled = new ArrayList<Order>();
            Boolean orderCanBeFulfilled = true;
            while (orderCanBeFulfilled) {
                Order firstBuyOrder = buyOrders.poll();
                Order firstSellOrder = sellOrders.poll();
                buyOrders.add(firstBuyOrder);
                sellOrders.add(firstSellOrder);
                // Buy price is higher than sell price
                // Sell at buy price 
                if (firstBuyOrder.getPrice() >= firstSellOrder.getPrice()) {
                    if (firstBuyOrder.getQuantity() >= firstSellOrder.getQuantity()) {
                        // sell order is fulfilled but buy order is not
                        Integer qty = firstBuyOrder.getQuantity() - firstSellOrder.getQuantity(); 
                        if (qty > 0) { //order is not fulfilled
                            Order newFirstBuyOrder = new Order(firstBuyOrder.getPlacedAtTimestamp(), firstBuyOrder.getPrice(), qty, true, firstBuyOrder.getDestination(), firstBuyOrder.getOrderId());
                            buyOrders.poll();
                            buyOrders.add(newFirstBuyOrder);
                        } else { // order is fulfilled. Check if SOR

                            buyOrders.poll(); 
                            // System.out.println("Fulfilled order");
                            if (firstBuyOrder.getIsSOR() == true) {
                                ordersFulfilled.add(firstBuyOrder);
                                System.out.println("[" + firstBuyOrder.getOrderId() + "]" + "[Lit] Buy" + firstBuyOrder.getQuantity() + firstBuyOrder.getPrice());
                            }
                        }
                    } else {
                        // buy order is fulfilled but sell order is not
                        Integer qty = firstSellOrder.getQuantity() - firstBuyOrder.getQuantity();
                        if (qty > 0) { //order is not fulfilled
                            Order newFirstSellOrder = new Order(firstSellOrder.getPlacedAtTimestamp(), firstSellOrder.getPrice(), qty, false, firstSellOrder.getDestination(), firstSellOrder.getOrderId());
                            sellOrders.poll();
                            sellOrders.add(newFirstSellOrder);
                        } else { // order is fulfilled. Check if SOR
                            sellOrders.poll();
                            // System.out.println("Fulfilled order");
                            Order newFirstSellOrder = new Order(firstSellOrder.getPlacedAtTimestamp(), firstBuyOrder.getPrice(), qty, false, firstSellOrder.getDestination(), firstSellOrder.getOrderId());
                            if (firstBuyOrder.getIsSOR() == true) {
                                ordersFulfilled.add(newFirstSellOrder);
                                System.out.println("[" + newFirstSellOrder.getOrderId() + "]" + "[Lit] Sell" + newFirstSellOrder.getQuantity() + newFirstSellOrder.getPrice());
                            }
                        }
                    }
                } else {
                    break;
                }
    
            }
    
            return ordersFulfilled;
        });
    }

}


