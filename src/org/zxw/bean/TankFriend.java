package org.zxw.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.zxw.business.Direction;
import org.zxw.business.Target;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.SoundUtils;

/**
*@author xiangweizhang
*@date 2017年5月13日---下午6:20:00
*@version 1.0
*/
public class TankFriend extends Tank implements Target{
	private long lastMoveTime;
	private Direction targetDirec;
	
	public TankFriend() {};
	
	public TankFriend(String imagePath, int x, int y) {
		super(imagePath,x,y);
	} 
	
	public void setTargetDirec(Direction targetDirec) {
		this.targetDirec = targetDirec;
	}
	
	@Override
	public Direction getTargetDirec() {
		return targetDirec;
	}
	
	
	private boolean canMove(ArrayList<Element> list,Direction direc) {
		for (Element e : list) {
			if (e instanceof Bullet) {
				int eX = e.getX();
				int eY = e.getY();
				int eW = e.getWidth();
				int eH = e.getHeigth();
				
				int x = this.x;
				int y = this.y;
				int w = this.width;
				int h = this.height;
				int speed = this.speed;
				
				//预判坦克下一步走会不会撞上子弹
				switch (direc) {
				case UP:
					y -= speed;
					break;
				case DOWN:
					y += speed;
					break;
				case RIGHT:
					x += speed;
					break;
				case LEFT:
					x -= speed;
					break;
				default:
					break;
				}
				
				boolean isCollied =
						CollisionUtils.isCollsionWithRect(eX, eY, eW, eH, 
								                          x, y, w, h);
				if (isCollied) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Bullet autoShot() {
		int randomTimeInterval = new Random().nextInt(2)*5000 + 5000; 
		long currentTime = System.currentTimeMillis();
		Bullet bullet;
		if (currentTime - lastShotTime >= randomTimeInterval) {
			bullet = new Bullet(this);
			try {
				SoundUtils.play(Constant.FIRE_MUSIC);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			lastShotTime = currentTime;
		} else {
			bullet = null;
		}
		
		return bullet;
	} 
	

	public void autoMove(ArrayList<Element> list) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMoveTime >= 500) {
			Direction direc = null;
			if (direc == null) {
				int seed = new Random().nextInt(4) + 1; //随机生成1-4之间的数字
				switch (seed % 4) {
				case 0:
					direc = Direction.UP;
					break;
				case 1:
					direc = Direction.DOWN;
					break;
				case 2:
					direc = Direction.RIGHT;
					break;
				case 3:
					direc = Direction.LEFT;
					break;
				default:
					break;
				}
			}
			
			if (this.canMove(list, direc)) {
				super.move(direc);
				lastMoveTime = currentTime;
			}
		}
	}

}

