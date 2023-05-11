import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Injector {

    SimulatedExchangeLit simulatedExchangeLit;

    SimulatedExchangeDark simulatedExchangeDark;



    public Injector(SimulatedExchangeLit simulatedExchangeLit, SimulatedExchangeDark simulatedExchangeDark) {

        this.simulatedExchangeLit = simulatedExchangeLit;
        this.simulatedExchangeDark = simulatedExchangeDark;

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

        quantity = Integer.parseInt(tagMap.get("38"));
        orderId = tagMap.get("11");
        placedAtTimestamp = convertTimestamp(timeStr);
        price = Double.parseDouble(tagMap.get("44"));
        isBuyOrSell = tagMap.get("54").equals("1") ? true : false;
        destination = tagMap.get("100");

        return new Order(placedAtTimestamp, price, quantity, isBuyOrSell, destination, orderId);

    }

    public void read() {

        // BufferedReader br = new BufferedReader(new
        // FileReader("testdata_orders_simpler_case"));

        try {
            File file = new File("testdata_orders_simpler_case.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            String[] tempArr;

            int count = 0;

            while ((line = br.readLine()) != null) {
                tempArr = line.split(",");
                // 1st idx is the time
                String timeStr = tempArr[0];

                // remaining are orders
                for (int i = 1; i < tempArr.length; i++) {
                    String orderStr = tempArr[i];
                    // for the orderStr, convert it to an Order
                    Order order = mapToOrder(orderStr, timeStr);
                    System.out.println(order.toString());
                }       
                count++;

                

                
            }

            System.out.println("Total number of files read: " + count);

            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // public static void main(String[] args) throws Exception {
    //     read();

    // }
}

