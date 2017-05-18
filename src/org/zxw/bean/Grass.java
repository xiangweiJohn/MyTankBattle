package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Acrossable;
import org.zxw.game.utils.DrawUtils;

public class Grass extends Element implements Acrossable {
	
	public Grass() {};
	
	public Grass(String imagePath, int x, int y) {
		super(imagePath,x,y);
	} 
	
	public int renderClass() {
		return 1;
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
