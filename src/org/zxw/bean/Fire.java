package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.business.Direction;
import org.zxw.business.Hiddenable;
import org.zxw.business.Treasure;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class Fire extends Element implements Destroyable,Treasure,Collapsible{
   
	private int blood = 3;
	private int addFire; 
	private Direction targetDirec;
	
	public Direction getTargetDirec() {
		return targetDirec;
	}


	public void setTargetDirec(Direction targetDirec) {
		this.targetDirec = targetDirec;
	}


	public Fire(String imagePath, int x , int y) {
		super(imagePath,x,y);
		switch (imagePath) {
		case Constant.IMAGE_FIRE_1:
			addFire = 1;
			break;
		case Constant.IMAGE_FIRE_2:
			addFire = 2;
			break;
		case Constant.IMAGE_FIRE_3:
			addFire = 3;
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public int renderClass() {
		
		return -1;
	}


	@Override
	public void draw() {
		try {
			DrawUtils.draw(imagePath, x, y);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}


	@Override
	public boolean isDestroyed() {
		
		return this.blood <= 0;
	}


	@Override
	public Blast showBlast() {
		try {
			SoundUtils.play(Constant.HIT_MUSIC);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		blood--;
		Blast blast = null;
		if (blood > 0) {
			blast = new Blast(this,true);
		} else {
			blast = new Blast(this,false);
		}
		return blast;
	}


	@Override
	public boolean isHided(Hiddenable hide) {
		Element e = (Element)hide;
		int eX = e.getX();
		int eY = e.getY();
		
		int x = this.x;
		int y = this.y;
		
		boolean isHided = false;
		if (eX == x && eY == y ) {
			isHided = true;
		}
		return isHided;
	}


	@Override
	public boolean isEaten(Tank tank) {
		int tankX = tank.getX();
		int tankY = tank.getY();
		int tankW = tank.getWidth();
		int tankH = tank.getHeigth();
		
		int x = this.x;
		int y = this.y;
		int w = this.width;
		int h = this.height;
		boolean isEaten = 
				CollisionUtils.isCollsionWithRect(tankX, tankY, tankW, tankH, 
						                              x, y, w, h);
		if (isEaten) {
			try {
				SoundUtils.play(Constant.ADD_MUSIC);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		return isEaten;
	}


	public int getAddFire() {
		return addFire;
	}

}
