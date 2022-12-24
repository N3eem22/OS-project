/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepingta;

/**
 *
 * @author mahmo
 */
public class Barber implements Runnable{
    
    private Shop shop;
    private int BA_ID;

    public Barber(Shop shop, int BA_ID) {
        this.shop = shop;
        this.BA_ID = BA_ID;
    }
    
    @Override
    public void run(){
        while (true) {            
            shop.GetHairCut(BA_ID);
        }
    }
    
}
