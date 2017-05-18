package org.zxw.game;

import org.zxw.constant.Constant;

public class Game {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameWindow game = 
				new GameWindow(Constant.GAME_TITLE, Constant.GAME_WIDTH, 
						       Constant.GAME_HEIGHT, Constant.GAME_FPS);
		game.start();
	}

}
