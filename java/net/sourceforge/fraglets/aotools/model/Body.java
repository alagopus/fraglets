/*
 * Body.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 3. August 2001, 18:31
 */

package net.sourceforge.fraglets.aotools.model;

import java.io.Serializable;

public class Body implements Serializable{
    
    public static final int HEAD = 0, EYE = 1, EAR = 2, CHEST = 3, WAIST = 4, LWRIST = 5;
    public static final int RWRIST = 6, LARM = 7, RARM = 8, LHAND = 9, RHAND = 10;
    public static final int FEET = 11, LEG = 12;
    public static final String[] SLOT_NAMES = {"Head", "Eye", "Ear", "Chest",
    "Waist", "Left Wrist", "Right Wrist", "Left Arm", "Right Arm", "Left Hand", "Right Hand",
    "Feet", "Legs", "Unknown"};
    public static final int NUM_SLOTS = 13;
    
    public Body(){
        clusterSlot = new Implant[13];
    }
    
    public Implant getImplant(int slot) {
        return clusterSlot[slot];
    }
    
    public void setImplant(int slot, Implant implant){
        clusterSlot[slot]=implant;
    }

    public String toString(){
        String result = "BODY:";
        
        for(int i = 0; i < NUM_SLOTS; i++){
            result += "\n"+ SLOT_NAMES[i]+":"+clusterSlot[i];
        }
        return result;
    }
    
    private Implant[] clusterSlot;
}
