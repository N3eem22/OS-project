/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepingta;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mahmo
 */
public class Customer implements Runnable{

    private int CustomerId;
    private Shop shop;

    public Customer(Shop shop) {
        this.shop = shop;
    }



    public void setCustomerId(int CustomerId) {
        this.CustomerId = CustomerId;
    }



    public int getCustomerId() {
        return CustomerId;
    }




    @Override
    public void run(){
        try {
            CutsHair();
        } catch (InterruptedException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void CutsHair() throws InterruptedException {							//customer is added to the list

        shop.EnterShop(this);
    }

}
