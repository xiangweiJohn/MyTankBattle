package org.zxw.bean;

import java.io.IOException;

import org.zxw.game.utils.DrawUtils;

public abstract class Element {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected String imagePath;
	
	public Element() {};
	
	public Element(String imagePath,int x, int y ) {
		this.x = x;
		this.y = y;
		this.imagePath = imagePath;
		try {
			this.width = DrawUtils.getSize(imagePath)[0];
			this.height = DrawUtils.getSize(imagePath)[1];
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeigth() {
		return height;
	}
	
	public abstract void draw();
	
	/*
	 * 渲染
	 */
	public int renderClass() {
		return 0;
	}
}
