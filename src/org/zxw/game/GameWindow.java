package org.zxw.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.zxw.bean.Blast;
import org.zxw.bean.Blood;
import org.zxw.bean.Bullet;
import org.zxw.bean.Element;
import org.zxw.bean.EndBackground;
import org.zxw.bean.Fire;
import org.zxw.bean.Grass;
import org.zxw.bean.Steel;
import org.zxw.bean.Tank;
import org.zxw.bean.TankEnemy;
import org.zxw.bean.TankFriend;
import org.zxw.bean.Wall;
import org.zxw.bean.Water;
import org.zxw.business.Blockable;
import org.zxw.business.Collapsible;
import org.zxw.business.Destroyable;
import org.zxw.business.Direction;
import org.zxw.business.Hiddenable;
import org.zxw.business.Moveable;
import org.zxw.business.Treasure;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class GameWindow extends Window{

	private ArrayList<Element> list = new ArrayList<>();
	//private Tank tank;
	private TankFriend tank;
	private TankEnemy tankE1,tankE2,tankE3,tankE4;
	private EndBackground eb;
	private boolean isWin;
	private boolean autoNavigate;

	public GameWindow(String title, int width, int height, int fps) {
		super(title, width, height, fps);
		
	}

	@Override
	protected void onCreate() {
		//播放背景音乐会
		try {
			SoundUtils.play(Constant.START_MUSCIC);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		//绘制"张"字地图的元素添加进集合
		myLastNameMap();
		
		//添加我方坦克元素进集合
		//tank = new Tank(Constant.IMAGE_TANK_U_PATH, Constant.GAME_WIDTH/2, Constant.GAME_HEIGHT - 64);
		tank = new TankFriend(Constant.IMAGE_TANK_U_PATH, Constant.GAME_WIDTH/2, Constant.GAME_HEIGHT - 64);
		list.add(tank);
		
		//添加敌方坦克元素进集合
		addEnemy();
		
		//for test
		/*for (int i = 0; i < Constant.GAME_WIDTH;i++) {
			list.add(new Grass(Constant.IMAGE_BLOOD_1,i*64,Constant.GAME_HEIGHT/2));
		}
		list.add(new TankEnemy(Constant.IMAGE_ENEMY_1_D, Constant.GAME_WIDTH/2, 0));
		
*/		
		//添加血量,火力等元素
		addTreasure();
		
		//结束时的图片和音乐
		eb = new EndBackground();
		
		//渲染,为了让坦克可以隐藏在草坪里
		list.sort(new Comparator<Element>() {

			@Override
			public int compare(Element e1, Element e2) {
				return e1.renderClass() - e2.renderClass();
			}
		});
	}

	private void addTreasure() {
		int count = 6;
		for (int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			if (e instanceof Hiddenable) {
				int eX = e.getX();
				int eY = e.getY();
				switch (count) {
				case 6:
					list.add(new Blood(Constant.IMAGE_BLOOD_1, eX, eY));
					break;
				case 5:
					list.add(new Fire(Constant.IMAGE_FIRE_1, eX, eY));
					break;
				case 4:
					list.add(new Blood(Constant.IMAGE_BLOOD_2, eX, eY));
					break;
				case 3:
					list.add(new Fire(Constant.IMAGE_FIRE_2, eX, eY));
					break;
				case 2:
					list.add(new Blood(Constant.IMAGE_BLOOD_3, eX, eY));
					break;
				case 1:
					list.add(new Fire(Constant.IMAGE_FIRE_3, eX, eY));
					return;
				}
				i += new Random().nextInt(4) + 8;//为了让Treasure不要挨着分布的地图中
				count--;
			}
		}
	}

	private void addEnemy() {
		while (true) {
			int seed1 = new Random().nextInt(Constant.GAME_WIDTH/64);//随机生成0-17之间的数字
			int seed2 = new Random().nextInt(Constant.GAME_HEIGHT/64);//随机生成0-9之间的数字
			int x = seed1 * 64;
			int y = seed2 * 64;
			int w = 0;
			int h = 0;
			try {
				 w = DrawUtils.getSize(Constant.IMAGE_ENEMY_1_U)[0];
				 h = DrawUtils.getSize(Constant.IMAGE_ENEMY_1_U)[1];
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			/*
			 * 在地图空白区域或没有阻挡功能的元素区域生成敌方坦克
			 * 首先遍历集合元素,如果遍历完都没有任何元素和生成的的敌方坦克位置冲突,
			 * 则可以在该位置生成敌方坦克
			 */
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				int x2 = e.getX();
				int y2 = e.getY();
				int w2 = e.getWidth();
				int h2 = e.getHeigth();
				boolean isCollided = 
						CollisionUtils.isCollsionWithRect(x, y, w, h,
						                                x2, y2, w2, h2);
				if (isCollided) {
					if (x == x2 && y == y2 && !(e instanceof Blockable)) {//在没有阻挡区域生成坦克
						if (tankE1 == null) {
							tankE1 = new TankEnemy(Constant.IMAGE_ENEMY_1_U,x,y);
							list.add(tankE1);
							i--;
							} else if (tankE2 == null) {
								tankE2 = new TankEnemy(Constant.IMAGE_ENEMY_1_D,x,y);
								list.add(tankE2);
								i--;
							} else if (tankE3 == null) {
								tankE3 = new TankEnemy(Constant.IMAGE_ENEMY_1_R,x,y);
								list.add(tankE3);
								i--;
								break;
							} else if (tankE4 == null) {
								tankE4 = new TankEnemy(Constant.IMAGE_ENEMY_1_L,x,y);
								list.add(tankE4);
								i--;
								return;
							}
					}
					break;
				} else if (i == list.size() - 1) {//在空白区域生成坦克
					if (tankE1 == null) {
					tankE1 = new TankEnemy(Constant.IMAGE_ENEMY_1_U,x,y);
					list.add(tankE1);
					i--;
					} else if (tankE2 == null) {
						tankE2 = new TankEnemy(Constant.IMAGE_ENEMY_1_D,x,y);
						list.add(tankE2);
						i--;
					} else if (tankE3 == null) {
						tankE3 = new TankEnemy(Constant.IMAGE_ENEMY_1_R,x,y);
						list.add(tankE3);
						i--;
						break;
					} else if (tankE4 == null) {
						tankE4 = new TankEnemy(Constant.IMAGE_ENEMY_1_L,x,y);
						list.add(tankE4);
						i--;
						return;
					}
				}
			}
		}
	}
	
	private void myLastNameMap() {
		int width = Math.round(Constant.GAME_WIDTH/64/2f) - 2;//7
	    int height = Math.round(Constant.GAME_HEIGHT/64/2f) - 3;//2
	   
	    //绘制"弓"字
	    //绘制"弓"字的一横
		for (int i = 1; i <= width; i++) {
			//list.add(new Wall(Constant.imageWallPath,64 * i, 64 * 1));
			int x = 64 * i;
			int y = 64 * 1;
			addElement(x, y);
		}
		
		//绘制"弓"字的一竖
		for (int i = 1; i <= height; i++) {
			//list.add(new Wall(randomImage(4),64 * width, 64 * (i +1)));
			int x = 64 * width;
			int y = 64 * (i +1);
			addElement(x, y);
		}
		
		//绘制"弓"字的一横
		for (int i = 1; i <= width; i++) {
			//list.add(new Wall(randomImage(4),64 * i, 64 * (heigth + heigth)));
			int x = 64 * i;
			int y = 64 * (height + height);
			addElement(x, y);
		}
		
		
		//绘制"弓"字的一竖
		for (int i = 1; i <= height; i++) {
			//list.add(new Wall(randomImage(4),64 * 1, 64 * (i + heigth +2)));
			int x = 64 * 1;
			int y = 64 * (i + height +2);
			addElement(x, y);
		}
		
		
		//绘制"弓"字的一横
		for (int i = 1; i <= width; i++) {
			//list.add(new Wall(randomImage(4),64 * i,64 *(heigth * 2 + 3)));
			int x = 64 * i;
			int y = 64 * (height * 2 + 3);
			addElement(x, y);
		}
		
		
		//绘制"弓"字的一竖
		for (int i = 1; i <= height; i++) {
			//list.add(new Wall(randomImage(4),64 * width,64 * (i + heigth * 2 + 3)));
			int x = 64 * width;
			int y = 64 * (i + height * 2 + 3);
			addElement(x, y);
		}
		
		
		//绘制"弓"字的一横
		for (int i = 4; i < width; i++ ) {
			//list.add(new Wall(randomImage(4),64 * i,64 * (heigth * 2 + 5)));
			int x = 64 * i;
			int y = 64 * (height * 2 + 5);
			addElement(x, y);
		}
		
		//绘制"长"字  张
		//绘制"长"字的一横
		for (int i = 1; i <= width + 3; i++ ) {
			if (i != 5) {
				//list.add(new Wall(randomImage(4),64 * (i + width - 1),64 * (heigth + 3) + 32));
				int x = 64 * (i + width - 1);
				int y = 64 * (height + 3) + 32;
				addElement(x, y);
			}
		}
		
		//绘制"长"字的一竖
		for (int i = 1; i <= height * 4 + 1; i++) {
			//list.add(new Wall(randomImage(4), 64 * (width + 4), 64 * i));
			int x = 64 * (width + 4);
			int y = 64 * i;
			addElement(x, y);
		}
		
		//绘制"长"字的一提
		for (int i = 1; i <= width - 5; i ++) {
			//list.add(new Wall(randomImage(4),64 * (width + 4 + i),64 * (Constant.gameHeigth/64 - 1)));
			int x = 64 * (width + 4 + i);
			int y = 64 * (Constant.GAME_HEIGHT/64 - 1);
			addElement(x, y);
		}
		
		//绘制"长"字的一丿
		for (int i = 1; i <= height + 2; i ++) {
			//list.add(new Wall(randomImage(4),64 * ( i + width + 4) + 32,64 * (5 - i)));
			int x = 64 * ( i + width + 4) + 32;
			int y = 64 * (5 - i);
			addElement(x, y);
		}
		
		//绘制"长"字的一捺
		for (int i = 1; i <= height + 2; i ++) {
			if (i == height + 2) {
				//list.add(new Wall(randomImage(4),64 * ( i + width + 4) + 32,64 * (6 + i) - 64));
				int x = 64 * ( i + width + 4) + 32;
				int y = 64 * (6 + i) - 64;
				addElement(x, y);
			} else {
				//list.add(new Wall(randomImage(4),64 * ( i + width + 4) + 32,64 * (6 + i) - 25));
				int x = 64 * ( i + width + 4) + 32;
				int y = 64 * (6 + i) - 25;
				addElement(x, y);
			} 
		}
	}

	private void addElement(int x , int y) {
		int seed = new Random().nextInt(4) +1;//生成1到num之间的随机数
		switch (seed % 4) {
		case 0:
			list.add(new Wall(Constant.IMAGE_WALL_PATH, x, y));
			break;
		case 1:
			list.add(new Water(Constant.IMAGE_WATER_PATH, x, y));
			break;
		case 2:
			list.add(new Grass(Constant.IMAGE_GRASS_PATH, x, y));
			break;
		case 3:
			list.add(new Steel(Constant.IMAGE_STEEL_PATH, x, y));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onMouseEvent(int key, int x, int y) {
	}

	@Override
	protected void onKeyEvent(int key) {
		if (!autoNavigate) {
			switch (key) {
			case Keyboard.KEY_UP:
				tank.move(Direction.UP);
				break;
			case Keyboard.KEY_DOWN:
				tank.move(Direction.DOWN);
				break;
			case Keyboard.KEY_RIGHT:
				tank.move(Direction.RIGHT);
				break;
			case Keyboard.KEY_LEFT:
				tank.move(Direction.LEFT);
				break;
			case Keyboard.KEY_SPACE:
				Bullet bullet = tank.shot();
				if (bullet != null) {
					for (int i = 0; i < tank.getFire(); i++) {
						list.add(bullet);
					}
				}
		    }
		}
		
		//添加我方坦克自动巡航功能   即自动移动和自动射击
		if (key == Keyboard.KEY_N) {
			autoNavigate = true;
		}
		
		//停止我方坦克自动巡航功能
		if (key == Keyboard.KEY_S) {
			autoNavigate = false;
		}
		
	}

	/*
	 * 绘制地图
	 */
	private void drawMap() {
		for (Element e : list) {
			e.draw();
		}
		
	}
	
	/*
	 * 实时销毁具有可销毁功能的物体
	 */
	private void destroyElement() {
		//实时销毁具有可销毁功能的物体
		Iterator<Element> it = list.iterator();
		while (it.hasNext()) {
			Element e = it.next();
			if (e instanceof Destroyable && ((Destroyable)e).isDestroyed()) {
				it.remove();
				//System.out.println("销毁....");
			}
		}
		/*for(int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			if (e instanceof Destroyable && ((Destroyable)e).isDestroyed()) {
				list.remove(e);
				i--;
				//System.out.println("销毁....");
			}
		}*/
	}
	
	/*
	 * 实时判断子弹和可碰撞物是否碰撞
	 */
	private void isCollided() {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				if (list.get(i) instanceof Bullet && 
					list.get(j) instanceof Collapsible) {
					Bullet bullet = (Bullet)list.get(i);
					Collapsible coll = (Collapsible)list.get(j);
					
					//子弹和子弹自己不碰撞
					if (bullet == coll) {
						continue;
					}
					
					/*
					 * 判断coll是不是Treasure
					 * 如果是,则判断Treasure这时是否显现在地图上,
					 * 如果显现,则子弹可以碰撞,如果没有,则将此coll赋值为在Treasure上的具有Hiddenable的元素
					 * @author xiangweihzhang
					 */
					if (coll instanceof Treasure) {
						for (int k = 0 ; k < list.size(); k++) {
							if (list.get(k) instanceof Hiddenable) {
								Treasure trea = (Treasure)coll;
								Hiddenable hide = (Hiddenable)list.get(k);
								if (trea.isHided(hide)) {
									coll = (Collapsible)hide;
									break;
								}
							}
						}
					} 
					
					/*
					 * 判断子弹和可碰撞物是否碰撞
					 */
					if (bullet.isCollided(coll)) {
						//销毁子弹
						list.remove(bullet);
						//System.out.println("子弹碰撞...");
						
						//添加爆炸物
						Blast blast = coll.showBlast();
						list.add(blast);
						i--;
						j--;
					}
				}
			}
		}
	}
	
	/*
	 * 实时判断可阻挡物是否阻挡可移动物体
	 */
	private void isBlocked() {
		for (Element e1: list) {
			for (Element e2 : list) {
				if (e1 instanceof Moveable && e2 instanceof Blockable) {
					Moveable move = (Moveable)e1;
					Blockable block = (Blockable)e2;
						if (!move.isMoved(block)) {
							break;
						}
				}
			}
		}
	}
	
	private void tankEnemyAction() {
		for (int i = 0; i < list.size(); i++) {
			Element e = list.get(i);
			if (e instanceof TankEnemy) {
				TankEnemy tankE = (TankEnemy)e;
				//tankE.move();
				tankE.autoMove(list);
				
				//Bullet bullet = tankE.shot();
				Bullet bullet = tankE.autoShot(list);
				if (bullet != null) {
					for (int j = 0; j < tankE.getFire(); j++) {
						list.add(bullet);
					}
			    }
			}
		}
	}
	
	//实时判断坦克是否吃了Treasure
	private void isTankEatTreasure() {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				if (list.get(i) instanceof Tank && 
					list.get(j) instanceof Treasure) {
					Tank tank = (Tank)list.get(i);
					Treasure trea = (Treasure)list.get(j);
					if (trea.isEaten(tank)) {
						tank.addTreasure(trea);
						list.remove(trea);
						i--;
						j--;
					}
				}
			}
		}
	}
	
	private void isWin() {
		int tankCount = 0;
		int tankEcount = 0;
		for (Element e : list) {
			if (e instanceof Tank ) {
				if (e instanceof TankEnemy) {
					tankEcount++;
				} else {
					tankCount++;
				}
			}
			if (tankCount != 0 && tankEcount != 0) {
				return;
			}
		}
		
		if (tankCount != 0 && tankEcount == 0) {
			//list.clear();//从 JDK version 1.1 开始，由 removeAll() 取代。
			list.removeAll(list);
			isWin = true;
		} else if (tankCount == 0 && tankEcount != 0){
			//list.clear();
			list.removeAll(list);
			isWin = false;
		}
	}
	
	private void isAutoNavigate() {
		if (autoNavigate) {
			Bullet bullet = tank.autoShot();
			if (bullet != null) {
				for (int j = 0; j < tank.getFire(); j++) {
					list.add(bullet);
				}
		    }
			
			tank.autoMove(list);
		}
	}
	
	@Override
	protected void onDisplayUpdate() {
		if (!list.isEmpty()) {
			//绘制地图
			drawMap();
			
			//实时销毁具有可销毁功能的物体
			destroyElement();
			
			//实时判断子弹和可碰撞物是否碰撞
			isCollided();

			//实时判断可阻挡物是否阻挡可移动物体
			isBlocked();
			
			//地方坦克随机开炮和随机移动
			tankEnemyAction(); 
			
			//实时判断坦克是否吃了Treasure
			isTankEatTreasure();
			
			//判断我方是否开启自动巡航功能
			isAutoNavigate();
			
			//判断最后是输是赢
			isWin();
			
			/*
			 * 1:解决程序运行时间长了后卡顿的问题  ok
			 * 2:敌方坦克发现了敌人后,根据血量是否走向敌方坦克方向射击敌人 ok
			 * 3:添加自己方坦克自动巡航功能  OK
			 * 4:敌方坦克发现了Treasure后,就会前去吃了它!  ok
			 */
			
		} else {
			eb.draw(isWin);
		}
	}

}
