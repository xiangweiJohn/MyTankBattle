package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Acrossable;
import org.zxw.game.utils.DrawUtils;

public class Water extends Element implements Acrossable{
	
	public Water() {};
	
	public Water(String imagePath, int x, int y) {
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

}
