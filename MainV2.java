public class MainV2 {

    public static void main(String[] args) {
        
        SimulatedExchangeDark simulatedExchangeDark = new SimulatedExchangeDark();
        SimulatedExchangeLit simulatedExchangeLit = new SimulatedExchangeLit();
        SmartOrderRouter smartOrderRouter = new SmartOrderRouter(simulatedExchangeLit, simulatedExchangeDark);
        InjectorV2 injectorV2 = new InjectorV2(simulatedExchangeLit, simulatedExchangeDark, smartOrderRouter);
        injectorV2.main();
        

    }
}