package org.zxw.bean;

import java.io.IOException;

import org.zxw.constant.Constant;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

/**
*@author xiangweizhang
*@date 2017年5月12日---下午4:32:33
*@version 1.0
*/
public class EndBackground extends Element {

	private boolean flag = true;
	
	public EndBackground() {
		try {
			this.width = DrawUtils.getSize(Constant.WINNER)[0];
			this.height = DrawUtils.getSize(Constant.WINNER)[1];
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		this.x = Constant.GAME_WIDTH/2 - this.width/2;
		this.y = Constant.GAME_HEIGHT/2 - this.height/2;
	}

	private void playMusic(boolean isWinner) {
		if (isWinner) {
			try {
				SoundUtils.play(Constant.WINNER_MUSIC);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} else {
			try {
				SoundUtils.play(Constant.LOSER_MUSIC);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void draw(boolean isWinner) {
		if (flag) {
			flag = false;
			this.playMusic(isWinner);
		}
		
		try {
			if (isWinner) {
				DrawUtils.draw(Constant.WINNER, x, y);
			} else {
				DrawUtils.draw(Constant.LOSER, x, y);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
		

	@Override
	public void draw() {
	}

}
