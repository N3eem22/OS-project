package sleepingta;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Shop {
    private final ReentrantLock mutex = new ReentrantLock();
    private int waitingChairs, noOfBA, availableBA;
    private int TotalHairCuts, BackLaterCounter;
    private List<Customer> CustomerList;
    private List<Customer> CustomerBackLater; //de msh mohema
    private Semaphore Availabe;
    private Random r = new Random();
    private Session form;

    public Shop(int nChairs, int nBA, int nCustomer, Session form) {
        this.waitingChairs = nChairs;
        this.noOfBA = nBA;
        this.availableBA = nBA;
        this.form = form;
        Availabe = new Semaphore(availableBA);
        this.CustomerList = new LinkedList<Customer>();
        this.CustomerBackLater = new ArrayList<Customer>(nCustomer); //msh 3yzen
    }



    public int getTotalHairCuts() { //mlhash lazma
        return TotalHairCuts;
    }

    public int getBackLaterCounter() {   //laa
        return BackLaterCounter;
    }

    public void GetHairCut(int BA_ID){
        Customer customer;


        synchronized(CustomerList){
            while (CustomerList.size() == 0) {
                form.SleepBA(BA_ID);
                System.out.println("\nBA "+BA_ID+" is waiting "
                        + "for the customer and sleeps in his chair");
                try {
                    CustomerList.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            customer = (Customer)((LinkedList<?>)CustomerList).poll();
            System.out.println("Customer "+customer.getCustomerId()+
                    " finds Barber available and "
                    + "the Barber "+BA_ID);
        }
        int Delay;
        try {
            if (Availabe.tryAcquire() && CustomerList.size() == waitingChairs){
                Availabe.acquire();
            }
            form.BusyBA(BA_ID);
            System.out.println("Barber "+BA_ID+" cut Customer's hair "+
                    customer.getCustomerId());

            double val = r.nextGaussian() * 2000 + 4000;
            Delay = Math.abs((int) Math.round(val));
            Thread.sleep(Delay);

            System.out.println("\nDone Cut Hair of "+
                    customer.getCustomerId()+" by Barber " +
                    BA_ID +" in "+(int)(Delay/1000)+ " seconds.");
            mutex.lock();
            try {
                TotalHairCuts++; //total al hala2lhom
            } finally {
                mutex.unlock();
            }

            if (CustomerList.size() > 0) {
                form.ReturnChair(BA_ID);
            }
            Availabe.release();

        } catch (InterruptedException e) {
        }



    }



    //msjh m3ana

    public void EnterShop(Customer customer){


        synchronized(CustomerList){
            if (CustomerList.size() == waitingChairs) {

                System.out.println("\nNo chair available "
                        + "for Customer "+customer.getCustomerId()+
                        " so Customer leaves and will come back later");

                CustomerBackLater.add(customer);
                mutex.lock();
                try {
                    BackLaterCounter++;
                } finally {
                    mutex.unlock();
                }
                return;
            }
            else if (Availabe.availablePermits() > 0 ) { //lw al hala2 fady fawa2le wahd w hato ygawble as2lo
                ((LinkedList<Customer>)CustomerList).offer(customer);
                CustomerList.notify();
            }
            else{
                try {
                    ((LinkedList<Customer>)CustomerList).offer(customer); //kol el hala2en mshgholen fa al customer hyfdl 2a3d
                    form.TakeChair();
                    System.out.println("All Barbers are busy so Customer "+
                            customer.getCustomerId()+
                            " takes a chair in Shop");

                    if (CustomerList.size() == 1) {
                        CustomerList.notify();
                    }
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        }
    }

    public List<Customer> Backlater(){
        return CustomerBackLater;
    }



}