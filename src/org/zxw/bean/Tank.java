package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Blockable;
import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.business.Direction;
import org.zxw.business.Moveable;
import org.zxw.business.Treasure;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class Tank extends Element implements Moveable,Blockable,Collapsible,Destroyable{
	
	protected int speed = 32;
	protected Direction direction = Direction.UP;
	protected long lastShotTime;
	
	private int freeSpace = 0;
	//记录错误的前行方向
	private Direction badDirection = null;
	protected int blood = 25;
	private int fire = 1;
	
	public Tank() {};
	
	public Tank(String imagePath, int x, int y) {
		super(imagePath,x,y);
	} 

	public void move(Direction direc) {
		
		//如果目前错误的方向不为空 并且 即将走的方向和错误方向一致,那就只允许走剩余的距离
		if(badDirection != null && badDirection == direc) { //注意:使用的是传入的方向和错误的方向比较,不是成员变量的方向
			switch (direc) {
			case UP:
				y -= this.freeSpace;
				break;
			case DOWN:
				y += this.freeSpace;
				break;
			case LEFT:
				x -= this.freeSpace;
				break;
			case RIGHT:
				x += this.freeSpace;
				break;
			default:
				break;
			}
			return ; //细节:终止方法
		}

		//如果是第一次改变方向,则不移动
		if (direc != this.direction) {
			this.direction = direc;
			return;
		}

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
		
		//坦克越界处理
		if (y >= Constant.GAME_HEIGHT - 64) {
			y = Constant.GAME_HEIGHT - 64;
		} else if (y <= 0) {
			y = 0;
		}
		if (x >= Constant.GAME_WIDTH - 64) {
			x = Constant.GAME_WIDTH - 64;
		} else if (x <= 0) {
			x = 0;
		}
	}
	
	protected Direction getDirection() {
		return this.direction;	
	}
	
	public Bullet shot(){
		long currentTime = System.currentTimeMillis();
		Bullet bullet;
		if (currentTime - lastShotTime >= 400) {
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
	
	public boolean isMoved(Blockable block) {
		Element e = (Element)block;
		//当当前的Blockable是自己是,返回true.表示可移动
		if (this == e) {
			return true;
		}
		int eX = e.getX();
		int eY = e.getY();
		int eW = e.getWidth();
		int eH = e.getHeigth();
		
		int x1 = this.x;
		int y1 = this.y;
		int w1 = this.width;
		int h1 = this.height;
		
		//预判下一步可移动物体是否碰上可阻挡物
		switch (this.direction) {
		case UP:
			y1 -= speed;
			break;
		case DOWN:
			y1 += speed;
			break;
		case RIGHT:
			x1 += speed;
			break;
		case LEFT:
			x1 -= speed;
			break;
		default:
			break;
		}
		
		boolean isCollided = 
				CollisionUtils.isCollsionWithRect(eX, eY, eW, eH, 
						                           x1, y1, w1, h1);
		boolean isMoved = !isCollided;//碰撞了就不能移动了
		
		//计算剩余可走距离
		if (isCollided) {
			this.badDirection = this.direction; //记录错误的方向
			switch (this.direction) {
			case UP:
				freeSpace = this.y - eY - eH;
				break;
			case DOWN:
				freeSpace = eY - this.y - this.height;
				break;
			case RIGHT:
				freeSpace = eX - this.x - this.width;
				break;
			case LEFT:
				freeSpace = this.x - eX - eW;
				break;
			default:
				break;
			}
		} else {
			this.freeSpace = 0;
			this.badDirection = null; //如果其他方向及时清空
		}
		
		return isMoved;
	}
	
	

	@Override
	public Blast showBlast() {
		
		try {
			SoundUtils.play(Constant.HIT_MUSIC);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		blood --;
		Blast blast = null;
		if (blood  > 0) {
			blast = new Blast(this,true);
		} else {
			blast = new Blast(this,false);
		}
		return blast;
	}
	
	public void addTreasure(Treasure trea) {
		if (trea instanceof Blood) {
			Blood blood = (Blood)trea; 
			this.blood += blood.getAddBlood();
		} else if (trea instanceof Fire) {
			Fire fire = (Fire)trea;
			this.fire += fire.getAddFire();
		}
	}

	public int getFire() {
		return fire;
	}

	@Override
	public boolean isDestroyed() {
		
		return blood <= 0;
	}
	
	@Override
	public void draw() {
		switch (this.direction) {
		case UP:
			imagePath = Constant.IMAGE_TANK_U_PATH;
			break;
		case DOWN:
			imagePath = Constant.IMAGE_TANK_D_PATH;
			break;
		case RIGHT:
			imagePath = Constant.IMAGE_TANK_R_PATH;
			break;
		case LEFT:
			imagePath = Constant.IMAGE_TANK_L_PATH;
			break;
		default:
			break;
		}
		
		try {
			DrawUtils.draw(imagePath, x, y);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
