/*
 * Implant.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 3. August 2001, 18:31
 */

package net.sourceforge.fraglets.aotools.model;

import java.io.Serializable;

public class Implant implements Serializable{
    
    private NanoCluster fadedCluster;
    private NanoCluster brightCluster;
    private NanoCluster shiningCluster;
    
    private int slot;
    private int quality;
    private int price;

    public Implant(int slot){
        this.slot = slot;
    }
    
    public int getSlot(){
            return slot;
        }


    public int getQuality(){
            return quality;
        }

    public void setQuality(int quality){
            this.quality = quality;
        }

    public int getPrice(){
            return price;
        }

    public void setPrice(int price){
            this.price = price;
        }

    public NanoCluster getFadedCluster() {
        return fadedCluster;
    }
    
    public void setFadedCluster(NanoCluster c){
        fadedCluster = c;
    }

    public NanoCluster getBrightCluster(){
            return brightCluster;
        }

    public void setBrightCluster(NanoCluster brightCluster){
            this.brightCluster = brightCluster;
        }

    public NanoCluster getShiningCluster(){
            return shiningCluster;
        }

    public void setShiningCluster(NanoCluster shiningCluster){
            this.shiningCluster = shiningCluster;
        }

    public int getTotalPrice() {
        return price+shiningCluster.getPrice()+brightCluster.getPrice()+
        shiningCluster.getPrice();
    }

    public String toString(){
        return "Q:"+quality+":"+shiningCluster+":"+brightCluster+":"+fadedCluster;
    }

}
