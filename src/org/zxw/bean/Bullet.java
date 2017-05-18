package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;//千万不要导错包(同样的包名)!!!
import org.zxw.business.Direction;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class Bullet extends Element implements Destroyable,Collapsible {

	private Direction direction;
	private int speed = 10;
	
	public Bullet(Tank tank) {
		int tankX = tank.getX();
		int tankY = tank.getY();
		int tankW = tank.getWidth();
		int tankH = tank.getHeigth();
	
		this.direction = tank.getDirection();
		
		switch (this.direction) {
		case UP:
			try {
				this.width = DrawUtils.getSize(Constant.IMAGE_BULLET_U_PATH)[0];
				this.height = DrawUtils.getSize(Constant.IMAGE_BULLET_U_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			this.x = tankX + tankW/2 - this.width/2;
			this.y = tankY - this.height;
			this.imagePath = Constant.IMAGE_BULLET_U_PATH;
			break;
		case DOWN:
			try {
				this.width = DrawUtils.getSize(Constant.IMAGE_BULLET_D_PATH)[0];
				this.height = DrawUtils.getSize(Constant.IMAGE_BULLET_D_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			this.x = tankX + tankW/2 - this.width/2;
			this.y = tankY + tankH;
			this.imagePath = Constant.IMAGE_BULLET_D_PATH;
			break;
		case RIGHT:
			try {
				this.width = DrawUtils.getSize(Constant.IMAGE_BULLET_R_PATH)[0];
				this.height = DrawUtils.getSize(Constant.IMAGE_BULLET_R_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			this.x = tankX + tankW;
			this.y = tankY + tankH/2 - this.height/2;
			this.imagePath = Constant.IMAGE_BULLET_R_PATH;
			break;
		case LEFT:
			try {
				this.width = DrawUtils.getSize(Constant.IMAGE_BULLET_L_PATH)[0];
				this.height = DrawUtils.getSize(Constant.IMAGE_BULLET_L_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			this.x = tankX - this.width;
			this.y = tankY + tankH/2 - this.height/2;
			this.imagePath = Constant.IMAGE_BULLET_L_PATH;
			break;
		default:
			break;
		}
	
	} 
	
	//判断子弹是否需要销毁
	public boolean isDestroyed() {
		if (this.x <= -this.width || this.x >= Constant.GAME_WIDTH || 
		    this.y <= -this.height || this.y >= Constant.GAME_HEIGHT) {
			return true;
		} else {
			return false;
		}
	}
	
	//判断子弹是否和具有可碰撞物相撞
	public boolean isCollided(Collapsible coll) {
		//得到可碰撞物的坐标和大小   
		Element e = (Element)coll;
		int collX = e.getX();
		int collY = e.getY();
		int collW = e.getWidth();
		int collH = e.getHeigth();
		
		//得到子弹的坐标和大小
		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeigth();
		
		//判断子弹和可碰撞物是否碰撞
		boolean isCollided = 
				CollisionUtils.isCollsionWithRect(collX, collY, collW, 
						                          collH, x, y, w, h);
		return isCollided;
	}
	
	@Override
	public void draw() {
		try {
			DrawUtils.draw(this.imagePath, x, y);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		switch (this.direction) {
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
	}

	@Override
	public Blast showBlast() {
		try {
			SoundUtils.play(Constant.BLAST_MUSIC);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	
		return new Blast(this,false);
	}

}
