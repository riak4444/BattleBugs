package BattleBugs;
import java.util.ArrayList;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import info.gridworld.actor.Actor;

public class Gary extends BattleBug2012
{
    int puCount = 0;
    public Gary(int str, int def, int spd, String name, Color col)
    {
            super(str, def, spd, name, col);
    }
    public void act()
    {
        
        // whether the bug should attack or not
        boolean shouldAttack = false;
        // whether the bug is going after a power up or no
        boolean gettingPU = false;
        // whether the bug is running away from an enemy or not
        boolean runningAway = false;
        
        // declare a Location named goTo and initialize it with the location (5,5)
        Location goTo = new Location(5, 5);
        
        // storing all the power up locations on the grid
        ArrayList<Location> puLocs = getPowerUpLocs();
        
        // storing the strength and defense power up locations on the grid
        ArrayList<PowerUp> powerUps  = new ArrayList<PowerUp>();
        ArrayList<Location> certainPULocs  = new ArrayList<Location>();
        for(PowerUp curr : powerUps)
        {
            if(curr.getColor().equals(Color.RED) || curr.getColor().equals(Color.GREEN))
            {
                certainPULocs.add(curr.getLocation());
            }
        } // end for
        
        //Location secondClosestPULoc = new Location(0,0);
        
        // storing the nearby BattleBugs on the grid
        ArrayList<Actor> actors = getActors();
        
        ArrayList<TombStone> tombstones = new ArrayList<TombStone>();
        for(Actor curr : actors)
        {
            if(curr instanceof TombStone)
            {
                tombstones.add((TombStone)curr);
            }
        } // end for
        
        ArrayList<BattleBug> enemies = new ArrayList<BattleBug>();
        for(Actor curr : actors)
        {
            if(curr instanceof BattleBug)
            {
                enemies.add((BattleBug)curr);
            }
        } // end for

        
        // order of tasks by priority:
        // 1. avoiding faling rocks.
        // 2. running away from a stronger enemy nearby.
        // 3. chasing and killing a weaker bug nearby.
        // 4. getting the nearest power up.
        // All of these must avoid obstacles along the way.
        
        
        // TASK 1: Avoiding the falling rocks
        int myRow = getLocation().getRow();
        int myCol = getLocation().getCol();
        
        if(rocksAboutToFall())
        {
            int[] theRocks = rocksLocs();
            // top row
            if(myRow == theRocks[1])
            {
                // top left corner
                if(myCol == theRocks[0])
                {
                    goTo = new Location(myRow+1, myCol+1);
                }
                // top right corner
                else if(myCol == theRocks[2])
                {
                    goTo = new Location(myRow+1, myCol-1);
                }
                // just the top
                else{
                    goTo = new Location(myRow+1, myCol);
                }
            } // end if
            
            // bottom row
            else if(myRow == theRocks[3])
            {
                // bottom left corner
                if(myCol == theRocks[0])
                {
                    goTo = new Location(myRow-1, myCol+1);
                }
                // bottom right corner
                else if(myCol == theRocks[2])
                {
                    goTo = new Location(myRow-1, myCol-1);
                }
                // just the bottom
                else{
                    goTo = new Location(myRow-1, myCol);
                }
            } // end else if
            
            // just the left
            else if(myCol == theRocks[0])
            {
                goTo = new Location(myRow, myCol+1);
            } // end else if
            
            // just the right
            else if(myCol == theRocks[2])
            {
                goTo = new Location(myRow, myCol-1);
            } // end else if
        } // end outer if
        
        
        else if(enemies.size() > 0)
        {
            // there is an enemy close by
            int ind = 0;
            ArrayList<BattleBug> weakerBugs = new ArrayList<BattleBug>();
            ArrayList<BattleBug> strongerBugs = new ArrayList<BattleBug>();
            for(BattleBug bug : enemies)
            {
                if(this.getStrength() - enemies.get(ind).getDefense() >= 3 && enemies.get(ind).getStrength() - this.getDefense() <= 2)
                {
                    // the enemy can be killed if attacked
                    // will add all the weaker bugs into weakerBugs
                    weakerBugs.add(bug);
                }
                else if(enemies.get(ind).getStrength() - this.getDefense() >= 3)
                {
                    // my bug can be killed by a stronger bug
                    // will add all the stronger bugs into strongerBugs
                    strongerBugs.add(bug);
                }
            } // end for
            
            // TASK 2: Getting away from the closest enemy that can kill my bug.
            if(strongerBugs.size() > 0)
            {                
                BattleBug strongBug = strongerBugs.get(0);
                double minDist = Integer.MAX_VALUE;
                for(BattleBug bug : strongerBugs)
                {
                    double distToBug = distanceTo(bug);
                    if(distToBug < minDist)
                    {
                        minDist = distToBug;
                        strongBug = bug;
                    }
                }
                
                // if could attack the enemy, shouldAttack should be true
                if(couldAttack(strongBug) && getDirection() == getDirectionToward(strongBug.getLocation()))
                {
                    shouldAttack = true;
                    goTo = strongBug.getLocation();
                }
                else
                {
                    runningAway = true;
                    
                    // goTo should be the opposite directions
                    
                    // check if my bug right against a wall
                    
                    int enRow = strongBug.getLocation().getRow();
                    int enCol = strongBug.getLocation().getCol();
                    
                    // above the enemy
                    if(myRow < enRow)
                    {
                        // above and left of the enemy
                        if(myCol < enCol)
                        {
                            goTo = new Location(myRow-1, myCol-1);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[1] == myRow)
                            {
                                goTo = new Location(myRow, myCol-1);
                                if(rocksLocs()[0] == myCol)
                                {
                                    goTo = new Location(myRow+1, myCol);
                                }
                            }
                            else if(rocksLocs()[0] == myCol)
                            {
                                goTo = new Location(myRow-1, myCol);
                            }
                        }
                        // above and right of the enemy
                        else if(myCol > enCol)
                        {
                            goTo = new Location(myRow-1, myCol+1);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[1] == myRow)
                            {
                                goTo = new Location(myRow, myCol+1);
                                if(rocksLocs()[2] == myCol)
                                {
                                    goTo = new Location(myRow+1, myCol);
                                }
                            }
                            else if(rocksLocs()[2] == myCol)
                            {
                                goTo = new Location(myRow-1, myCol);
                            }
                        }
                        // directly above the enemy
                        else{
                            goTo = new Location(myRow-1, myCol);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[1] == myRow)
                            {
                                if(myCol <= 13)
                                {
                                    goTo = new Location(myRow, myCol+1);
                                }
                                else
                                {
                                    goTo = new Location(myRow, myCol-1);
                                }
                            }
                        }
                    }
                    
                    // below the enemy
                    else if(myRow < enRow)
                    {
                        // below and left of the enemy
                        if(myCol < enCol)
                        {
                            goTo = new Location(myRow+1, myCol-1);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[3] == myRow)
                            {
                                goTo = new Location(myRow, myCol-1);
                                if(rocksLocs()[0] == myCol)
                                {
                                    goTo = new Location(myRow-1, myCol);
                                }
                            }
                            else if(rocksLocs()[0] == myCol)
                            {
                                goTo = new Location(myRow+1, myCol);
                            }
                        }
                        // below and right of the enemy
                        else if(myCol > enCol)
                        {
                            goTo = new Location(myRow+1, myCol+1);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[3] == myRow)
                            {
                                goTo = new Location(myRow, myCol+1);
                                if(rocksLocs()[2] == myCol)
                                {
                                    goTo = new Location(myRow-1, myCol);
                                }
                            }
                            else if(rocksLocs()[2] == myCol)
                            {
                                goTo = new Location(myRow+1, myCol);
                            }
                        }
                        // directly below the enemy
                        else{
                            goTo = new Location(myRow+1, myCol);
                            
                            // check if my bug right against a wall
                            if(rocksLocs()[3] == myRow)
                            {
                                if(myCol <= 13)
                                {
                                    goTo = new Location(myRow, myCol+1);
                                }
                                else
                                {
                                    goTo = new Location(myRow, myCol-1);
                                }
                            }
                        }
                    }
                    
                    // just to the left
                    else if(myCol < enCol)
                    {
                        goTo = new Location(myRow, myCol-1);
                        
                        // check if my bug right against a wall
                        if(rocksLocs()[0] == myCol)
                        {
                            if(myRow <= 13)
                            {
                                goTo = new Location(myRow+1, myCol);
                            }
                            else
                            {
                                goTo = new Location(myRow-1, myCol);
                            }
                        }
                    }
                    
                    // just to the right
                    else if(myCol > enCol)
                    {
                        goTo = new Location(myRow, myCol+1);
                        
                        // check if my bug right against a wall
                        if(rocksLocs()[2] == myCol)
                        {
                            if(myRow <= 13)
                            {
                                goTo = new Location(myRow+1, myCol);
                            }
                            else
                            {
                                goTo = new Location(myRow-1, myCol);
                            }
                        }
                    }
                    
                    
                    
                    // check if the opposite direction also has a sronger enemy
                    
                    // check if the opposite direction has an obstacle
                }
            } // end if
            
            
            // TASK 3: Chasing and killing the closest weaker bug near me.
            else if(weakerBugs.size() > 0)
            {
                BattleBug weakBug = weakerBugs.get(0);
                double minDist = Integer.MAX_VALUE;
                for(BattleBug bug : strongerBugs)
                {
                    double distToBug = distanceTo(bug);
                    if(distToBug < minDist)
                    {
                        minDist = distToBug;
                        weakBug = bug;
                    }
                }
                
                goTo = weakBug.getLocation();
                
                // attack if nearby
                if(couldAttack(weakBug))
                {
                    shouldAttack = true;
                }
            } // end else if
        } // end outer else if
        
        // TASK 4: Getting the closest power up.
        else
        {
            // once every 4 times the bug goes for power ups, it will go for the closest power up
            if(puLocs.size() > 0)
            {
                    // go to the nearest power up
                    double minDist = Integer.MAX_VALUE;
                    Location closestPULoc = puLocs.get(0);
                    int toBeRemoved = 0;
                    for(int i = 0; i < puLocs.size(); i++)
                    {
                        double distToPU = distanceTo(puLocs.get(i));
                        if(distToPU < minDist)
                        {
                            minDist = distToPU;
                            closestPULoc = puLocs.get(i);
                            //toBeRemoved = i;
                        }
                    }
                    goTo = closestPULoc;
                    
                    /*puLocs.remove(toBeRemoved);
                    
                    // finding the second closest power up
                    minDist = Integer.MAX_VALUE;
                    closestPULoc = puLocs.get(0);
                    for(int i = 0; i < puLocs.size(); i++)
                    {
                        double distToPU = distanceTo(puLocs.get(i));
                        if(distToPU < minDist)
                        {
                            minDist = distToPU;
                            secondClosestPULoc = puLocs.get(i);
                        }
                    }*/                    
            }
            
            // otherwise the bug will go for the closest strength or defense
            /*else
            {
                if(certainPULocs.size() > 0)
                {
                    // go to the nearest power up
                    double minDist = Integer.MAX_VALUE;
                    Location closestPULoc = certainPULocs.get(0);
                    for(Location curr : certainPULocs)
                    {
                        double distToPU = distanceTo(curr);
                        if(distToPU < minDist)
                        {
                            minDist = distToPU;
                            closestPULoc = curr;

                        }
                    }
                    goTo = closestPULoc;
                }
            } // end nested else*/
        } // end else
        
        
        
        //Call the getDirectionToward() method and store the result in a variable named dir.
        int dir = getDirectionToward(goTo);
        
        
        // TASK 5: avoiding the obstacles, I don't know if it works
        boolean obstacleExists = false;
        Actor obstacle = null;
        for(Actor curr : actors)
        {
            double distToObs = distanceTo(curr);
            int currRow = curr.getLocation().getRow();
            int currCol = curr.getLocation().getCol();  
            if((distToObs == Math.sqrt(2) || distToObs == 1) && dir == getDirectionToward(curr.getLocation()) && !(curr instanceof PowerUp))
            {
                obstacleExists = true;
                obstacle = curr;
            }
        }
        
        if(obstacleExists)
        {
            ArrayList<Location> empAdLocs = getEmptyAdjacentLocations();
            if(empAdLocs.size() > 0)
            {
                for(Location curr :  empAdLocs)
                {
                    if(Math.abs(curr.getRow() - obstacle.getLocation().getRow()) == 1 || Math.abs(curr.getCol() - obstacle.getLocation().getCol()) == 1)
                    {
                        int[] rl = rocksLocs();
                        if(!rocksAboutToFall() || (curr.getCol() != rl[0] && curr.getCol() != rl[2] && curr.getRow() != rl[1] && curr.getRow() != rl[3]))
                        {
                            goTo = curr;
                        }
                    }
                }
            }
        }
        
        /*if(actors.size() > 0)
        {
            Actor obstacle = null;
            for(Actor curr : actors)
            {
                double distToObs = distanceTo(curr);
                int currRow = curr.getLocation().getRow();
                int currCol = curr.getLocation().getCol();  
                if((distToObs == Math.sqrt(2) || distToObs == 1) && dir == getDirectionToward(curr.getLocation()))
                {
                    obstacle = curr;
                }
            }
            
            if(obstacle != null)
            {
                int obsRow = obstacle.getLocation().getRow();
                int obsCol = obstacle.getLocation().getCol();

                // above the obstacle
                if(myRow < obsRow)
                {
                    // above and left of the obstacle
                    if(myCol < obsCol)
                    {
                        goTo = new Location(myRow, myCol+1);
                    }
                    // above and right of the obstacle
                    else if(myCol > obsCol)
                    {
                        goTo = new Location(myRow, myCol-1);
                    }
                    // directly above the obstacle
                    else{
                        goTo = new Location(myRow+1, myCol+1);
                    }
                }

                // below the obstacle
                else if(myRow < obsRow)
                {
                    // below and left of the obstacle
                    if(myCol < obsCol)
                    {
                        goTo = new Location(myRow, myCol+1);
                    }
                    // below and right of the obstacle
                    else if(myCol > obsCol)
                    {
                        
                        System.out.println("below and right");
                        goTo = new Location(myRow, myCol-1);
                    }
                    // directly below the obstacle
                    else
                    {
                        goTo = new Location(myRow-1, myCol+1);
                    }
                }

                // just to the left of obstacle
                else if(myCol < obsCol)
                {
                    goTo = new Location(myRow-1, myCol+1);
                }

                // just to the right of obstacle
                else if(myCol > obsCol)
                {
                    goTo = new Location(myRow-1, myCol-1);
                }
            }
        } // end outer else*/
        

        //Using the getDirection() method check to see if your bug is facing the desired direction dir
        //If so then call the move() method
        //if not then call turnTo() method towards the desired direction dir.
        if(getDirection() == dir)
        {   
            if(shouldAttack)
            {
                attack();
                turn();
            }
            else if(runningAway)
            {
                move2();
            }
            else
            {
                move();
                if(gettingPU)
                {
                    if(getLocation().equals(goTo))
                    {
                        // incrementing puCount if the bug got the power up
                        puCount++;
                    }
                }
            }
        } // end outer if
        
        else
        {
            turnTo(dir);
            if(shouldAttack)
            {
                attack();
            }
        } // end else
    } // end method
    
    
    
    // HELPER METHODS
    public double distanceTo(BattleBug enemy)
    {
        // square root of ((x1 - x2)^2 + (y1-y2)^2)
        int myRow = getLocation().getRow();
        int myCol = getLocation().getCol();
        int enemyRow = enemy.getLocation().getRow();
        int enemyCol = enemy.getLocation().getCol();
        double result = Math.sqrt(Math.pow(myRow - enemyRow, 2) + Math.pow(myCol - enemyCol, 2));
        return result;
    }
    
    public double distanceTo(Actor thing)
    {
        // square root of ((x1 - x2)^2 + (y1-y2)^2)
        int myRow = getLocation().getRow();
        int myCol = getLocation().getCol();
        int thingRow = thing.getLocation().getRow();
        int thingCol = thing.getLocation().getCol();
        double result = Math.sqrt(Math.pow(myRow - thingRow, 2) + Math.pow(myCol - thingCol, 2));
        return result;
    }
    
    public double distanceTo(Location loc)
    {
        // square root of ((x1 - x2)^2 + (y1-y2)^2)
        int myRow = getLocation().getRow();
        int myCol = getLocation().getCol();
        int otherRow = loc.getRow();
        int otherCol = loc.getCol();
        double result = Math.sqrt(Math.pow(myRow - otherRow, 2) + Math.pow(myCol - otherCol, 2));
        return result;
    }
    
    public boolean rocksAboutToFall()
    {
        if(getNumAct()%40 >= 38)
        {
            return true;
        }
        else{
            return false;
        }
    }
    
    public int[] rocksLocs()
    {
        int num = getNumAct()/40;
        int leftCol = 0 + num;
        int topRow = 0 + num;
        int rightCol = 26 - num;
        int bottomRow = 26 - num;
        int[] rocks = {leftCol, topRow, rightCol, bottomRow};
        return rocks;
    }
    
    public boolean couldAttack(BattleBug enemy)
    {
        boolean couldAtt = false;
        
        int rowDist = Math.abs(enemy.getLocation().getRow() - getLocation().getRow());
        int colDist = Math.abs(enemy.getLocation().getCol() - getLocation().getCol());
        
        if(getStrength() < 10)
        {
            if((rowDist == 1 && colDist == 1)
                || (rowDist == 1 && colDist == 0)
                || (rowDist == 0 && colDist == 1))
            {
                couldAtt = true;
            }
        }
        else if(getStrength() >= 10 && getStrength() < 20)
        {
            
            if(((rowDist <= 2 && colDist == rowDist)
                || (rowDist <= 2 && colDist == 0)
                || (rowDist == 0 && colDist <= 2)))
            {
                couldAtt = true;
            }
        }
        else
        {
            if(((rowDist <= 3 && colDist == rowDist)
                || (rowDist <= 3 && colDist == 0)
                || (rowDist == 0 && colDist <= 3)))
            {
                couldAtt = true;
            }
        }
        
        return couldAtt;
    }
    
    public String victory()
    {
        String ret = "──¯¯──.\n" + " (0)(0)o ## o\\\n" + "  |  |  # $   |\n" + "  |__|\\_##___/\n" + " (_─_─_─_─_─_)";
        return ret;
    }
} // end class