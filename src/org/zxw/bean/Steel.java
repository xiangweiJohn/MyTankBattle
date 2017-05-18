package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Blockable;
import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.business.Hiddenable;
import org.zxw.game.utils.DrawUtils;

public class Steel extends Element implements Collapsible,Destroyable,
                                                Blockable,Hiddenable{
	private int blood = 6;
	
	public Steel() {};
	
	public Steel(String imagePath, int x, int y) {
		super(imagePath,x,y);
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
	public Blast showBlast() {
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
	public boolean isDestroyed() {
		
		return blood <= 0;
	}

}
