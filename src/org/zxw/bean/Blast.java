package org.zxw.bean;

import java.io.IOException;

import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.constant.Constant;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class Blast extends Element implements Destroyable{
	private boolean flag = false;//记录图片是否播放完
	private String[] images = {
			"res/img/blast_1.gif",
			"res/img/blast_2.gif",
			"res/img/blast_3.gif",
			"res/img/blast_4.gif",
			"res/img/blast_5.gif",
			"res/img/blast_6.gif",
			"res/img/blast_7.gif",
			"res/img/blast_8.gif"
	};
	private int index = 0; //图片索引
	
	public Blast(String imagePath, int x , int y) {
		super(imagePath, x, y);
	}

	public Blast(Collapsible coll,boolean isAlive) {
		Element e = (Element)coll; 
		int eX = e.getX();
		int eY = e.getY();
		int eW = e.getWidth();
		int eH = e.getHeigth();
		
		try {
			this.width = DrawUtils.getSize(Constant.BLAST_1_PATH)[0];
			this.height = DrawUtils.getSize(Constant.BLAST_1_PATH)[1];
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		this.x = eX + eW/2 - this.width/2;
		this.y = eY + eH/2 - this.height/2;
		
		if (isAlive) {
			images = new String[] {
					"res/img/blast_1.gif",
					"res/img/blast_2.gif",
					"res/img/blast_3.gif",
					"res/img/blast_4.gif",
			};
		}
		
		//this.imagePath = Constant.BLAST_4_PATH;
	}
	
	/*
	 * 当图片播放完了就销毁图片
	 */
	@Override
	public boolean isDestroyed() {
		if (flag) {
			try {
				SoundUtils.play(Constant.BLAST_MUSIC);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
		
		return flag ;
	}
	
	@Override
	public void draw() {
		try {
			if (index >= images.length) {
				index = 0;
				flag = true;
			}
			DrawUtils.draw(images[index], x, y);
			index++;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}

 
}
