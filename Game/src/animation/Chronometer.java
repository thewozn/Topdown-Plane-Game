/**
*
* @author: anthony.woznica
*
* Absolutely ANY USE of this project REQUIRE THIS CREDIT IN THE HEADER OF THE FILE (WOZNICA Anthony).
* There are NO exceptions.
*
* This file contains the object Chronometer influencing the number of incomming flights.
*
**/


package animation;


public class Chronometer {
	private int hh;
	private int min;
	
	public Chronometer(){
		this.hh = 16;
		this.min = 24;
	}
	
	public void update(){
		this.min++;
		if(this.min >= 60){
			this.min = 0;
			this.hh = (hh + 1 < 24) ? this.hh + 1 : 0;
		}
	}
	
	public int getMin(){
		return this.min;
	}
	
	public int getHh(){
		return this.hh;
	}
}
