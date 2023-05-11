public class Main {

    public static void main(String[] args) {

        SimulatedExchangeDark simulatedExchangeDark = new SimulatedExchangeDark();
        SimulatedExchangeLit simulatedExchangeLit = new SimulatedExchangeLit();
        Injector injector = new Injector(simulatedExchangeLit, simulatedExchangeDark);
        injector.read();
    }
}