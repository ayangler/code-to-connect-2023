import java.util.*;

public class SimulatedExchangeLit {

    private static final Comparator<Order> OrderComparator = new Comparator<Order>() {
        @Override
        public int compare(Order order1, Order order2) {
            if (order1.getPrice() < order2.getPrice()) {
                return -1;
            } else if (order1.getPrice() > order2.getPrice()) {
                return 1;
            } else {
                return order1.getPlacedAtTimestamp().compareTo(order2.getPlacedAtTimestamp());
            }
        }
    };

    public static PriorityQueue<Order> buyOrders = new PriorityQueue<Order>(OrderComparator);
    public static PriorityQueue<Order> sellOrders = new PriorityQueue<Order>(OrderComparator);

    public SimulatedExchangeLit(ArrayList<Order> orders) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getIsBuy() == true) {
                buyOrders.add(orders.get(i));
            } else {
                sellOrders.add(orders.get(i));
            }
        }
    }

    public PriorityQueue<Order> getBuyOrders() {
        return SimulatedExchangeLit.buyOrders;
    }

    public PriorityQueue<Order> getSellOrders() {
        return SimulatedExchangeLit.sellOrders;
    }

    public void cancelOrder(Order order) {
        if (order.getIsBuy() == true) {
            SimulatedExchangeLit.buyOrders.remove(order);
        } else {
            SimulatedExchangeLit.sellOrders.remove(order);
        }
    }
}


