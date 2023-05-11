import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class InjectorV2 {

    SimulatedExchangeLit simulatedExchangeLit;

    SimulatedExchangeDark simulatedExchangeDark;

    SmartOrderRouter smartOrderRouter;

    // public InjectorV2(SimulatedExchangeLit simulatedExchangeLit, SimulatedExchangeDark simulatedExchangeDark) {
    //     this.simulatedExchangeLit = simulatedExchangeLit;
    //     this.simulatedExchangeDark = simulatedExchangeDark;
    // }

    public InjectorV2(SimulatedExchangeLit simulatedExchangeLit, SimulatedExchangeDark simulatedExchangeDark, SmartOrderRouter smartOrderRouter) {

        this.simulatedExchangeLit = simulatedExchangeLit;
        this.simulatedExchangeDark = simulatedExchangeDark;
        this.smartOrderRouter = smartOrderRouter;

    }

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public void main() {
    
        try {
            System.out.println("???");
            // list of orders
            Queue<Order> queue = new LinkedList<>();
            BufferedReader br = new BufferedReader(new FileReader("testdata_orders_simpler_case.csv"));

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                String[] tempArr = line.split(",");
                // 1st idx is the time
                String timeStr = tempArr[0];

                // remaining are orders
                for (int i = 1; i < tempArr.length; i++) {
                    String orderStr = tempArr[i];
                    // for the orderStr, convert it to an Order
                    Order order = mapToOrder(orderStr, timeStr);
                    // System.out.println(order.toString());
                    queue.add(order);
                    System.out.println(order);
                    count++;

                }
            
            }

            System.out.println("FILES READ FROM CSV " + count);
            
            br.close();

            count = 0;
     
            LocalTime now = LocalTime.of(9, 0); // start at 9am
            LocalTime endTime = LocalTime.of(9, 5); // end at 9:05am
            // System.out.println(queue.size());
            while (now.compareTo(endTime) < 1 && !queue.isEmpty()) {
                
                // peek the topmost files

                while (!queue.isEmpty() && queue.peek().getTimestamp().equals(now)) {
                    // then route the file
                    Order polled = queue.poll();
                    route(polled);
                    count++;

                }
                now = now.plusNanos(1000000);

            }

            System.out.println("FILES ROUTED " + count);
            

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    
    private void route(Order order) {
        if (order.getDestination().equals("L")) {
            simulatedExchangeLit.updatePQ(order);
           
        }
        else if (order.getDestination().equals("SOR")) {
            System.out.println("Send to SOR");
            smartOrderRouter.updateOrders(order);
        }
    }

    public long convertTimestamp(String timestamp) {
        // System.out.println(timestamp);
        String[] components = timestamp.split(":");
        int hours = Integer.parseInt(components[0]);
        int minutes = Integer.parseInt(components[1]);
        String[] secondsAndMilliseconds = components[2].split("\\.");
        int seconds = Integer.parseInt(secondsAndMilliseconds[0]);
        int milliseconds = Integer.parseInt(secondsAndMilliseconds[1]);
        long totalMilliseconds = hours * 60 * 60 * 1000L + minutes * 60 * 1000L + seconds * 1000L + milliseconds;

        return totalMilliseconds;

    }

    private Map<String, String> getOrderTags(String order) {
        Map<String, String> tagMap = new HashMap<>();

        String[] tags = order.split(";");

        // split by ; to get ["tag=num", "tag=num"...]

        for (String tag : tags) {
            String[] tagAndNum = tag.trim().split("="); // split by "=" to get tag and num

            String tagKey = tagAndNum[0];
            String value = tagAndNum[1];

            tagMap.put(tagKey, value);
        }

        return tagMap;
    }

    public Order mapToOrder(String orderStr, String timeStr) {
        Map<String, String> tagMap = getOrderTags(orderStr);
        int quantity;
        String orderId;
        Long placedAtTimestamp;
        Double price;
        boolean isBuyOrSell;
        String destination;
        LocalTime timestamp;

        quantity = Integer.parseInt(tagMap.get("38"));
        orderId = tagMap.get("11");
        placedAtTimestamp = convertTimestamp(timeStr);
  
        timestamp = LocalTime.parse(timeStr, TIME_FORMAT);
        price = Double.parseDouble(tagMap.get("44"));
        isBuyOrSell = tagMap.get("54").equals("1") ? true : false;
        destination = tagMap.get("100");

        return new Order(placedAtTimestamp, price, quantity, isBuyOrSell, destination, orderId, timestamp);

    }
}
