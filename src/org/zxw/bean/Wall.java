package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Blockable;
import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.business.Hiddenable;
import org.zxw.constant.Constant;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class Wall extends Element implements Collapsible,Destroyable,
                                              Blockable,Hiddenable{
	
	//多态:子类的成员变量和父类的成员变量一样时,编译和运行都看父类的!!!
	/*private int x = 1;
	private int y;*/
	
	private int blood = 3;
	
	public int getBlood() {
		return blood;
	}

	public Wall(){};
	
	public Wall(String imagePath, int x, int y) {
		super(imagePath,x,y);
	} 
	
	@Override
	public boolean isDestroyed() {
		
		return blood <= 0;
	}
	
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
	public void draw() {
		try {
			DrawUtils.draw(imagePath, x, y);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
