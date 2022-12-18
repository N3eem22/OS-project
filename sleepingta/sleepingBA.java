
package sleepingta;


import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SleepingBA {

    private int noOfCustomers;
    private int noOfChairs;
    private int noOfBA;

    public SleepingBA(int noOfCustomers, int noOfChairs, int noOfBA) {
        this.noOfCustomers = noOfCustomers;
        this.noOfChairs = noOfChairs;
        this.noOfBA = noOfBA;
    }



    public void Start(Session form) throws InterruptedException{
        ExecutorService exec = Executors.newFixedThreadPool(12);
        Shop shop = new Shop(noOfChairs, noOfBA, noOfCustomers, form);
        Random r = new Random();

        System.out.println("Shop is opened with "+noOfBA+" Barbers");

        long startTime  = System.currentTimeMillis();

        for (int i = 1; i <= noOfBA; i++) {
            Barber BA = new Barber(shop, i);
            Thread thBA = new Thread(BA);
            exec.execute(thBA);
        }

        for (int i = 1; i <= noOfCustomers; i++) {
            try {
                Customer customer = new Customer(shop);
                customer.setCustomerId(i);
                Thread thCustomer = new Thread(customer);
                exec.execute(thCustomer);

                double val = r.nextGaussian() * 2000 + 2000;
                int Delay = Math.abs((int) Math.round(val));
                Thread.sleep(Delay);


            } catch (InterruptedException ex) {
                Logger.getLogger(SleepingBA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List<Customer> backLater = shop.Backlater();
        if (backLater.size() > 0 ) {
            for (int i = 0; i < backLater.size(); i++) {
                try {
                    Customer customer = backLater.get(i);
                    Thread thCustomer = new Thread(customer);
                    exec.execute(thCustomer);

                    double val = r.nextGaussian() * 2000 + 2000;
                    int Delay = Math.abs((int) Math.round(val));
                    Thread.sleep(Delay);


                } catch (InterruptedException ex) {
                    Logger.getLogger(SleepingBA.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        exec.awaitTermination(12, SECONDS);
        exec.shutdown();

        long elapsedTime = (System.currentTimeMillis() - startTime)/1000;

        System.out.println("Shop is closed");
        System.out.println("\nTotal time elapsed in seconds"
                + " for Cutting "+noOfCustomers+" customers' Hair by "
                +noOfBA+" Barbers with "+noOfChairs+
                " chairs in the Shop is: "
                +elapsedTime);
        System.out.println("\nTotal customers: "+noOfCustomers+
                "\nTotal customers served: "+shop.getTotalHairCuts()
                +"\nTotal customers returned: "+shop.getBackLaterCounter());
    }

}