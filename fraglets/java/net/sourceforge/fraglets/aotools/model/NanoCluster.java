/*
 * NanoCluster.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 3. August 2001, 18:31
 */

package net.sourceforge.fraglets.aotools.model;

import java.io.Serializable;

public class NanoCluster implements Serializable{
    
    private int quality;
    private int skillMod;
    private int price;
    private BaseNanoCluster baseCluster;
 
    public NanoCluster(BaseNanoCluster b){
        baseCluster = b;
    }
    
    public String getName(){
        return baseCluster.getName();
    }
    
    public String getSkill(){
        return baseCluster.getSkill();
    }
    public int getQuality(){
            return quality;
        }

    public void setQuality(int quality){
            this.quality = quality;
        }

    public int getSkillMod(){
            return skillMod;
        }

    public void setSkillMod(int skillMod){
            this.skillMod = skillMod;
        }

    public int getPrice(){
            return price;
        }

    public void setPrice(int price){
            this.price = price;
    }
    
    public String toString(){
        return "Q:"+quality+":"+baseCluster;
    }
        
        


}
