package net.sourceforge.fraglets.aotools.model;

import java.io.Serializable;

public class BaseNanoCluster implements Serializable{
    
    private String name;
    private String skill;
    private int type;
    private int bodyLoc;
    
    public static final String[] NANO_TYPES = { "Faded", "Bright", "Shining", "Unknown" };
    
    public BaseNanoCluster(String name, String skill, int type, int b){
        this.name = name;
        this.skill = skill;
        this.type = type;
        bodyLoc = b;
    }
    
    public String getName(){
            return name;
    }

    public String getSkill(){
            return skill;
    }
    
    public int getType(){
        return type;
    }
    
    public int getBodyLoc(){
        return bodyLoc;
    }
    
    public String toString(){
        return name;
    }
    
    public boolean equals(Object o){
        if(!(o instanceof BaseNanoCluster)){
            return false;
        }
        BaseNanoCluster b = (BaseNanoCluster)o;
        
        return ((b.name.equals(name))&&(b.skill.equals(skill))
                &&(b.type == type)&&(b.bodyLoc == bodyLoc));
    }
    
    public int hashCode(){
        return(
            name.hashCode()^
            skill.hashCode()^
            (new Integer(type)).hashCode()^
            (new Integer(bodyLoc)).hashCode()
            );
    }
        
}


