package de.cau.se;

public class SimpleDataGeneratorMain {

    public static void main(String[] args) throws Exception {
        final SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        simpleDataGenerator.createEventLogAndSendEvents();
        while ( true ) {
            Thread.sleep(10000);
            System.out.println("Finished");
        }
    }
}
