package BattleBugs;
import java.util.ArrayList;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import info.gridworld.actor.Actor;

public class Peter extends BattleBug2012
{
    public static final String RED = "\u001B[31m";
    int badURow, badDRow, badLCol, badRCol;
    boolean moveF, changedGT;
    public Peter(int str, int def, int spd, String name, Color col)
    {
            super(str, def, spd, name, col);
            badURow = -1;
            badDRow = 27;
            badLCol = -1;
            badRCol = 27;
            moveF = false;
            changedGT = false;
    }
    public void act()
    {       
        //updating the columns and rows to avoid
        updateBadRowsAndCols();
        System.out.println("\nBADUROW = " + badURow + "\nBADDROW = " + badDRow + "\nBADLCOL = " + badLCol + "\nBADRCOL = " + badRCol + "\nMY ACTS = " + getNumAct() + "\n");
        
        //INFO-----------------------------------------------------------------------------------------------------------------------------------------------------------------
        //setting a variable for where to go
        Location goTo = new Location(13, 13);
        
        //variable to choose when to shoot
        boolean shoot = false;
        
        //Getting all the powerups and storing them by color
        ArrayList<PowerUp> PUs = getPowerUps();
        ArrayList<PowerUp> RED = new ArrayList<PowerUp>(), BLUE = new ArrayList<PowerUp>(), GREEN = new ArrayList<PowerUp>();
        for(PowerUp pu : PUs)
        {
            Color c = pu.getColor();
            if(c.equals(Color.RED))
                RED.add(pu);
            else if(c.equals(Color.BLUE))
                BLUE.add(pu);
            else
                GREEN.add(pu);
        }        
        
        //getting the location of all the powerUps and the getting the closest
        //and furthest PU
        Location closestR = closestPU(RED), closestB = closestPU(BLUE), closestG = closestPU(GREEN);
        ArrayList<Location> puLocs = getPowerUpLocs();
        Location closestPu = null;
        Location furthestPu = null;
        double minDtoPu = Integer.MAX_VALUE, maxDtoPu = Integer.MIN_VALUE;
        for(Location pu : puLocs)
        {
            double dToPu = distanceTo(pu);
            if(dToPu < minDtoPu)
            {
                closestPu = pu;
                minDtoPu = dToPu;
            }
            else if(dToPu > maxDtoPu && pu.getRow()!=badURow && pu.getRow()!=badDRow && pu.getCol()!=badLCol && pu.getCol()!=badRCol)
            {
                furthestPu = pu;
                maxDtoPu = dToPu;
            }
        }
        
        
        //Storing all the Actors within my range
        ArrayList<Actor> actors = getActors();
        
        //Storing the enemies near me in a variable
        ArrayList<BattleBug> enemies = new ArrayList<BattleBug>();
        for(Actor a : actors)
        {
            if(a instanceof BattleBug)
                enemies.add((BattleBug)a);
        }
        
        //Making a boolean for whether or not i will chase the enemy
        boolean willChase = false, runAway = false;
        
        //getting the enemy and the location of the enemy closest to me
        BattleBug BB = null;
        Location cBug;
        double minDtoEn = Integer.MAX_VALUE;
        for(BattleBug bb : enemies)
        {
            double dist = distanceTo(bb.getLocation());
            if(dist < minDtoEn)
            {
                minDtoEn = dist;
                cBug = bb.getLocation();
                BB = bb;
            }
        }
        
        //Checking if the enemy nearest to me is weaker or stronger than me
        //ask q???
        boolean imStronger = false;      
        int mySt = getStrength(), mySp = getSpeed(), myD = getDefense();
        if(BB != null)
        {            
            int theirSt = BB.getStrength();
            int theirSp = BB.getSpeed();
            int theirD = BB.getDefense();
            if(mySt - theirD >= 3 || mySt - theirD >= 3 && theirSt - myD >= 3 )
            {
                imStronger = true;
            }
            else if(theirSt - myD >= 3)
            {
                runAway = shouldRunFrom(BB);
            }
        }       
        
        //Getting my current row and column
        int currR = getLocation().getRow(), currC = getLocation().getCol();
        
            
        //DECIDING WHERE TO GO------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        if(puLocs.size() > 0 && enemies.size() > 0)//ATTACK/CHASE/RUNAWAY || GETPU
        {
            if(minDtoEn < minDtoPu && imStronger)
            {                
                goTo = BB.getLocation();
                willChase = true;
            }
            else
            {
                if(getStrength() < 11 && closestR != null && distanceTo(closestR) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1) 
                {
                    goTo = closestR;
                }
                else if(getDefense() < 20 && closestB != null && distanceTo(closestB) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1)
                {
                    goTo = closestB;
                }
                else if(closestG != null && distanceTo(closestG) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1)
                {
                    goTo = closestG;
                }
                else
                {
                    if(closestPu != null)
                        goTo = closestPu;
                }
            }                
        }
        else if(enemies.size() > 0)//ATTACK/CHASE/RUNAWAY //=========================work on
        {
            if(minDtoEn < minDtoPu && imStronger)
            {                
                goTo = BB.getLocation();
                willChase = true;
            }
        }
        else if(puLocs.size() > 0)//GETPU
        {
                if(getStrength() < 11 && closestR != null && distanceTo(closestR) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1) 
                {
                    goTo = closestR;
                }
                else if(getDefense() < 20 && closestB != null && distanceTo(closestB) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1)
                {
                    goTo = closestB;
                }
                else if(closestG != null && distanceTo(closestG) - distanceTo(closestPu) < 7 && (int)distanceTo(closestPu) != 1)
                {
                    goTo = closestG;
                }
                else
                {
                    if(closestPu != null)
                        goTo = closestPu;
                }
        }
        System.out.println("\n\n================WILLSHOOT = " + shoot + "==================\n\n");
        if(goTo.getCol() == badLCol || goTo.getCol() == badRCol || goTo.getRow() == badURow || goTo.getRow() == badDRow)
            goTo = furthestPu;
        //Call the getDirectionToward() method and store the result in a variable named dir.
        //int dir = getDirectionToward(goTo);       //check this line..THINK OF WTD WHEN THERE IS NO LOC
        
        //PRIORITIZING GETTING UNSTSUCK, running away, AND AVOIDING ROCKS
                
        if(runAway && BB != null)
        {
            goTo = shouldRunTo(BB);   //TEMP
            System.out.println("===============================================I CHANGED MY LOC TO RUNAWAY================================================\nNEWGT = " + goTo);
        }
        //If i'm stuck, changing my goTo location ========================FIXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        if(!canMove())
        {
            double minD = Integer.MAX_VALUE, gtX = goTo.getCol(), gtY = goTo.getRow();
            //declaring a variable to hold the new location to go to
            Location newGT = null;
            //getting all the empty spaces adjacent to me
            ArrayList<Location> empty = getEmptyAdjacentLocations();
            
            //setting newGT to the empty location closest to current goTo
            for(Location loc : empty)
            {
                int locX = loc.getCol(), locY = loc.getRow();
                double currD = Math.sqrt(Math.pow(gtY-locY, 2) + Math.pow(gtX-locX, 2));
                if(currD < minD)
                {
                    newGT = loc;
                    minD = currD;
                }
            }
               
            if(newGT != null)
            {
                goTo = newGT;
                changedGT = true;
            }            
        }
        if(currR == badURow || currR == badDRow || currC == badLCol || currC == badRCol)
        {
            //AVOIDING THE ROCKS
            int newC, newR;
            if(currR == badURow)
                newR = currR+1;
            else if(currR == badDRow)
                newR = currR-1;
            else
                newR = currR;
            
            if(currC == badLCol)
                newC = currC+1;
            else if(currC == badRCol)
                newC = currC-1;
            else
                newC = currC;
            
            goTo = new Location(newR, newC);
        }
        
        
        //ACTUALLY MOVING----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Getting the distance to goTo
        double dist = distanceTo(goTo);
        //updating dir incase goTo changed
        int dir = getDirectionToward(goTo);
        
        //if GT changed
        if(changedGT)
        {            
            if(moveF == true)
            {                
                System.out.println(RED + "\n-----------I MOVED----------\n");
                if(canMove2Away() && ((int)dist == 2 || runAway))
                    move2();
                else
                    move(); 
                if(BB != null && canAttack(BB, imStronger, getDirection() == getDirectionToward(BB.getLocation())))
                    attack();
                moveF = false;
                changedGT = false;
            }
            else                
            {
                System.out.println(RED + "\n-----------I TURNEDDDDDD----------\n");
                turnTo(dir);
                
                if(BB != null && canAttack(BB, imStronger, getDirection() == getDirectionToward(BB.getLocation())))
                   attack();
                
                moveF = true;
            }
        }
        else if(getDirection() == dir)
        {          
            if(canMove2Away() && ((int)dist == 2 || runAway))
                move2();
            else
                move();  
            if(BB != null && canAttack(BB, imStronger, getDirection() == getDirectionToward(BB.getLocation())))
                    attack();
        }
        else
        {
            turnTo(dir);
            if(BB != null && canAttack(BB, imStronger, getDirection() == getDirectionToward(BB.getLocation())))
                    attack();
        }         
        
        System.out.println("\n*****************************IM GOING TO : " + goTo + "*************************************\n");
    }
    public double distanceTo(Location anotherLoc)
    {
        int dist;
        int x1 = anotherLoc.getCol();
        int x2 = getLocation().getCol();   
        int y1 = anotherLoc.getRow(), y2 = getLocation().getRow();
        double sum = Math.pow(y1-y2, 2) + Math.pow(x1- x2, 2);
        return Math.sqrt(sum);
    }
    //----------------FOR RUNNING-----------------
    //checking if i should runAway
    public boolean shouldRunFrom(BattleBug b)
    {
        boolean shouldRun = false;        
        int theirS = b.getStrength(), bStMinusMyD = b.getStrength() - getDefense();
        int distFromMe = (int)distanceTo(b.getLocation());
        if(bStMinusMyD >= 3)
        {
            if(distFromMe <= 2 && theirS < 10)
            {
                shouldRun = true;
            }
            else
            {
                if(distFromMe <= 4 && theirS >= 10)
                    shouldRun = true;
            }
        }
        return shouldRun;
    }
    //getting the location i should run to if i'm being chased
    public Location shouldRunTo(BattleBug b)
    {
        Location output = new Location(13, 13);
        boolean cm2A = canMove2Away(), cmA = canMove();
        int mySt = getStrength();
        boolean cm2 = mySt >= 10;
        int myX = getLocation().getCol(), myY = getLocation().getRow();
        int bX = b.getLocation().getCol(), bY = b.getLocation().getRow();
        
        if(cm2)
        {
            ArrayList<Location> locs = new ArrayList<Location>();
            locs.add(new Location(myY-2, myX-2));
            locs.add(new Location(myY-2, myX));
            locs.add(new Location(myY-2, myX+2));
            locs.add(new Location(myY, myX+2));
            locs.add(new Location(myY+2, myX+2));
            locs.add(new Location(myY+2, myX));
            locs.add(new Location(myY+2, myX-2));
            locs.add(new Location(myY, myX-2));
            
            //checking which are valid
            for(int i = 0; i < locs.size(); i++)
            {
                if(!isValidL(locs.get(i)))
                {
                    locs.remove(locs.get(i));
                    i--;
                }
            }
            
            //getting the one that is furthest from enemy
            double maxD = Integer.MIN_VALUE;
            for(Location l : locs)
            {
                int lX = l.getCol(), lY = l.getRow();
                double currD = Math.sqrt(Math.pow(lY-bY, 2) + Math.pow(lX-bX, 2));
                if(currD > maxD)
                {
                    maxD = currD;
                    output = l;
                }
            }
        }
        else if(!cm2)
        {
            ArrayList<Location> locs = new ArrayList<Location>();
            locs.add(new Location(myY-1, myX-1));
            locs.add(new Location(myY-1, myX));
            locs.add(new Location(myY-1, myX+1));
            locs.add(new Location(myY, myX+1));
            locs.add(new Location(myY+1, myX+1));
            locs.add(new Location(myY+1, myX));
            locs.add(new Location(myY+1, myX-1));
            locs.add(new Location(myY, myX-1));
            
            //checking which are valid
            for(int i = 0; i < locs.size(); i++)
            {
                if(!isValidL(locs.get(i)))
                {
                    locs.remove(locs.get(i));
                    i--;
                }
            }
            
            //getting the one that is furthest from enemy
            double maxD = Integer.MIN_VALUE;
            for(Location l : locs)
            {
                int lX = l.getCol(), lY = l.getRow();
                double currD = Math.sqrt(Math.pow(lY-bY, 2) + Math.pow(lX-bX, 2));
                if(currD > maxD)
                {
                    maxD = currD;
                    output = l;
                }
            }
        }
        return output;
    }
    public boolean isValidL(Location l)
    {
        int lX = l.getCol(), lY = l.getRow();
        if(rockFallTime() && getGrid().isValid(l) && lX != badLCol && lX != badRCol && lY != badURow && lY != badDRow)
            return true;
        else if(!rockFallTime() && getGrid().isValid(l))
            return true;
        return false;
    }
    public boolean rockFallTime()
    {
        return getNumAct() % 38 == 0 || getNumAct() %39 == 0;
    }
    //Checking if i should attack
    public boolean canAttack(BattleBug b, boolean stronger, boolean rtD)
    {
        boolean yesA = false;
        int dist = (int)distanceTo(b.getLocation());
        System.out.println("\n\n\ndist = " + dist + "\n\n\n");
        if(getStrength() < 10 && dist <= 1 && stronger && rtD)
            yesA = true;
        else if(getStrength() >= 10 && dist <= 2 && stronger && rtD)
            yesA = true;
        else if(getStrength() >= 20 && dist <= 4 && stronger && rtD)
            yesA = true;
        
        return yesA;
    }
    //Getting the closestPowerUp in an array of powerUps
    public Location closestPU(ArrayList<PowerUp> pu)
    {
        Location output = null;
        double minDtoPu = Integer.MAX_VALUE;
        if(pu.size() > 0)
        {
            for(PowerUp p : pu)
            {
                Location lOfP = p.getLocation();
                double dtoP = distanceTo(lOfP);
                if(dtoP < minDtoPu)                    
                {
                    minDtoPu = dtoP;
                    output = lOfP;
                }
            }
        }
        return output;
    }
    //updating the badRows and cols
   public void updateBadRowsAndCols()
   {
       if(getNumAct() != 0 && getNumAct() % 38 == 0)
       {
           badLCol++;
           badRCol--;
           badURow++;
           badDRow--;
       }
   }
}