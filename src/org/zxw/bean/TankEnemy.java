package org.zxw.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.zxw.business.Acrossable;
import org.zxw.business.Blockable;
import org.zxw.business.Collapsible;
import org.zxw.business.Direction;
import org.zxw.business.Hiddenable;
import org.zxw.business.Target;
import org.zxw.business.Treasure;
import org.zxw.constant.Constant;
import org.zxw.game.utils.CollisionUtils;
import org.zxw.game.utils.DrawUtils;
import org.zxw.game.utils.SoundUtils;

public class TankEnemy extends Tank {
	private long lastMoveTime;
	
	public TankEnemy(String imagePath, int x, int y) {
		super(imagePath,x,y);
	}
	
	/*
	 * 判断能不能吃到Treasure
	 * @author xiangweizhang
	 * @date 2017.5.14
	 */
	private  boolean canEatTeasure(Treasure trea,ArrayList<Element> list) {
		//拿到Treasure的坐标和大小方向
		Element e = (Element)trea;
		int treaX = e.getX();
		int treaY = e.getY();
		int treaW = e.getWidth();
		int treaH = e.getHeigth();
		Direction direc = ((Target)e).getTargetDirec();
		
		//拿到自己方的坐标和大小
		int x = this.x;
		int y = this.y;
		int w = this.width;
		int h = this.height;
		
		//拿到矩形的坐标和大小
		int recX = 0;
		int recY = 0;
		int recW = 0;
		int recH = 0;
		
		switch (direc) {
		case UP:
			recX = x;
			recY = treaY + treaH - 1;//加1的目的是延长矩形的高,这样可以避免trea旁边有Blockable的情况
			recW = w;
			recH = y - recY;
			break;
			
		case DOWN:
			recX = x;
			recY = y + h;
			recW = w;
			recH = treaY + 1 - recY;
			break;
			
		case RIGHT:
			recX = x + w;
			recY = y;
			recW = treaX + 1 - recX;
			recH = h;
			break;
			
		case LEFT:
			recX = treaX + treaW - 1;
			recY = y;
			recW = x - recX;
			recH = h;
			break;
		default:
			break;
		}
		
		for (Element e1 : list) {
			if (e1 instanceof Blockable) {
				int blockX = e1.getX();
				int blockY = e1.getY();
				int blockW = e1.getWidth();
				int blockH = e1.getHeigth();
				
				boolean iscollied = 
						CollisionUtils.isCollsionWithRect(recX, recY, recW, recH, 
								                       blockX, blockY, blockW, blockH);
				if (iscollied) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	/*
	 * 敌方坦克发射了子弹后坦克的走向问题,不要自己走去撞击bullet
	 */
	private boolean canMove(ArrayList<Element> list,Direction direc) {
		for (Element e : list) {
			if (e instanceof Bullet) {
				int eX = e.getX();
				int eY = e.getY();
				int eW = e.getWidth();
				int eH = e.getHeigth();
				
				int x = this.x;
				int y = this.y;
				int w = this.width;
				int h = this.height;
				int speed = this.speed;
				
				//预判坦克下一步走会不会撞上子弹
				switch (direc) {
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
				
				boolean isCollied =
						CollisionUtils.isCollsionWithRect(eX, eY, eW, eH, 
								                          x, y, w, h);
				if (isCollied) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/*
	 * 判断是否发现目标(这里的目标即自己控制的坦克,自己控制的坦克不一定只有一个):遍历集合,拿到目标的坐标,判断是否在我方的上下左右某个方向上(这里指的是正方向).
	 * 如果在,则判断,目标和我方坦克之间有没遮挡视线的物体,如果有,就终止判断,发现不了目标;如果没有,则可以发现目标
	 * @author xiangweizhang
	 * @date 2017.5.14
	 * 
	 * log:
	 * 2017.5.14
	 * 1:测试时发现有死机的情况,当目标和自己方坦克之间有子弹时发生! 
	 * 问题已解决:原因是在move(ArrayList<Element> list)这个方法里,
	 * 用了while,当己方发射了子弹后,同时又判断到目标的位置和子弹是同一个方向,
	 * this.canMove(list, direc)这个方法返回为false,那么就有可能一直在while循环里面出不来!
	 * 
	 * 2:和发现目标后向目标方向前进到不能前进了就一直射击,但是有转向的情况!
	 * 问题已解决,原因是子弹产生的爆炸物遮挡住视线,己方判断不出目标在哪里!但是这里的逻辑就错了,已经发现目标,爆炸物就不能作为遮挡物了!
	 * 
	 * 2017.5.15   
	 * 1:没考虑到一种情况,两种遮挡物可以组成一个遮挡物,这情况简直丧心病狂!!!
	 * 问题已解决
	 * 解决方法:将两个遮挡物组成一个遮挡物!
	 * 
	 * 2017.5.16
	 * 问题已解决
	 * 1:当敌人只有一部分出现在我方视野中时,这时只需有遮挡物遮挡住出现在我方视野中的敌人部分即可视为我方发现不了敌人
	 * 
	 * 2017.5.18
	 * 1:将Treasure也作为目标,当发现时,就前去吃掉   TankFriend实现Target接口和Treasure继承Target接口
	 */
	private Target FindTarget(ArrayList<Element> list) {
		//拿到自己的坐标和大小
		int x = this.x;
		int y = this.y;
		int w = this.width;
		int h = this.height;
		
		for (Element e : list) {
			if (e instanceof Target) {
				
				//如果e是Treasure的话,看e是否被隐藏起来
				if (e instanceof Treasure) {
					boolean isHided = false;
					for (int i = 0 ; i < list.size(); i++) {
						if (list.get(i) instanceof Hiddenable) {
							Treasure trea = (Treasure)e;
							Hiddenable hide = (Hiddenable)list.get(i);
							if (trea.isHided(hide)) {
								isHided = true;
								break;
							}
						}
					}
					if (isHided) {
						continue;
					}
				}
				
				//拿到目标元素
				int eX = e.getX();
				int eY = e.getY();
				int eW = e.getWidth();
				int eH = e.getHeigth();
				
				/*
				 * 判断目标是否在某个方向上的一个很重要的思想是:改变自己方的坐标和大小!!!
				 * 比如判断是否在上方:则此时 x不变 ,y=0,w不变,h=屏幕高度-y!!!
				 */
				
				//1:判断在哪个方向上
				Direction direc = Direction.UP;//初始方向给为上方
				while (direc != null) {
					switch (direc) {
					case UP:
						//拿到长矩形的大小和宽度
						int xU = x;
						int yU = 0;
						int wU = w;
						int hU = y;
						boolean isCollied = CollisionUtils.isCollsionWithRect(xU, yU, wU, hU,
								                             eX, eY, eW, eH);
						if (isCollied) {//判断到是在上方
							//目标和矩形碰撞并且在矩形内的部分
							int eIX = 0;
							int eIY = 0;
							int eIW = 0;
							int eIH = 0;
							
							if (eX == x) {//目标在正上方
								//拿到目标和矩形碰撞并且在矩形内的部分
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = eH;
							} else if (eX < x) {//目标在左上方
								eIX = x;
								eIY = eY;
								eIW = eX + eW - x;
								eIH = eH;
							} else {//目标在右上方
								eIX = eX;
								eIY = eY;
								eIW = x + w - eX;
								eIH = eH;
							}
							
							//拿到在矩形内的部分的图形和己方之间的处于中间位置的小矩形
							int xM = eIX;
							int yM = eIY + eIH;
							int wM = eIW;
							int hM = y - yM;
							
							//判断自己方和目标之间是否有阻挡物完全阻挡住视线,注意是完全遮挡住视线(即挡住敌人出现在视野中的部分)!!!
							for (Element e1 : list) {
								if (e1 == this || e1 == e || 
									e1 instanceof Water || e1 instanceof Blast) {//如果遍历到的遮挡物是自己或者是目标或者是水或者是爆炸物,这里东西列为不能遮挡视线的东西
									if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
										((Target)e).setTargetDirec(direc);
										return (Target)e;
									}
									continue;
								}
								
								//拿到遮挡物的坐标和大小
								int e1X = e1.getX();
								int e1Y = e1.getY();
								int e1W = e1.getWidth();
								int e1H = e1.getHeigth();
								
								if (e1Y <= yM + hM && e1Y + e1H >= yM) {//遮挡物至少有出现在我方坦克和敌人的中间
									if (e1X <= xM) {//左边的遮挡物
										if (e1X + e1W >= xM + wM) {//一个遮挡物即可
											//判断到在目标和我方之间有元素可能完全遮挡住视线
											direc = null;
											break;//虽然目标的一部分或全部出现在正上方的某个局域,但是有遮挡物完全遮挡住视线,还是发现不了,所以跳出寻找遮挡物的循环,继续执行寻找目标的循环
										} else if (e1X + e1W > xM && e1X + e1W < xM + wM) {//一个(左边)遮挡物只能遮挡一部分,继续寻找(右边)遮挡物来遮挡
											for (Element e2 : list) {
												if (e2 == this || e2 == e || 
													e2 instanceof Water || e2 instanceof Blast) {
													if (e2 == list.get(list.size()-1)) {
														break;
													}
													continue;
												}
												
												int e2X = e2.getX();
												int e2Y = e2.getY();
												int e2W = e2.getWidth();
												int e2H = e2.getHeigth(); 
												
												if (e2Y <= yM + hM && e2Y + e2H >= yM && //右边遮挡物满足的条件
													e2X <= e1X && e2X + e2W >= xM + wM) {
													direc = null;
													break;
												}
											}
											
											if (direc == null) {
												break;
											}
										}
									} else if (e1X > xM && e1X < xM + wM) {//右边的遮挡物
										for (Element e3 : list) {//寻找左边的遮挡物
											if (e3 == this || e3 == e || 
												e3 instanceof Water || e3 instanceof Blast) {
												if (e3 == list.get(list.size()-1)) {
													break;
												}
												continue;
											}
											
											int e3X = e3.getX();
											int e3Y = e3.getY();
											int e3W = e3.getWidth();
											int e3H = e3.getHeigth(); 
											
											if (e3Y <= yM + hM && e3Y + e3H >= yM &&
												e3X + e3W >= e1X && e3X <= xM) {
												direc = null;
												break;
											}
										}
										
										if (direc == null) {
											break;
										}
									}
									
								} 
								
								if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
									((Target)e).setTargetDirec(direc);
									return (Target)e;
								}
							}
						} else {
							direc = Direction.DOWN;
						}
						break;
						
					case DOWN:
						//拿到长矩形的大小和宽度
						int xD = x;
						int yD = y + h;
						int wD = w;
						int hD = Constant.GAME_HEIGHT - y - h;
						isCollied = CollisionUtils.isCollsionWithRect(xD, yD, wD, hD,
	                                                                  eX, eY, eW, eH);
						if (isCollied) {//判断到是在下方
							//目标和矩形碰撞并且在矩形内的部分
							int eIX = 0;
							int eIY = 0;
							int eIW = 0;
							int eIH = 0;
							
							if (eX == x) {//目标在正下方
								//拿到目标和矩形碰撞并且在矩形内的部分
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = eH;
							} else if (eX < x) {//目标在左下方
								eIX = x;
								eIY = eY;
								eIW = eX + eW - x;
								eIH = eH;
							} else {//目标在右下方
								eIX = eX;
								eIY = eY;
								eIW = x + w - eX;
								eIH = eH;
							}
							
							//拿到在矩形内的部分的图形和己方之间的处于中间位置的小矩形
							int xM = eIX;
							int yM = y + h;
							int wM = eIW;
							int hM = eY - yM;
							
							//判断自己方和目标之间是否有阻挡物完全阻挡住视线,注意是完全遮挡住视线!!!
							for (Element e1 : list) {
								if (e1 == this || e1 == e || 
									e1 instanceof Water || e1 instanceof Blast) {//如果遍历到的遮挡物是自己或者是目标或者是水或者是爆炸物,这里东西列为不能遮挡视线的东西
									if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
										((Target)e).setTargetDirec(direc);
										return (Target)e;
									}
									continue;
								}
								
								//拿到遮挡物的坐标和大小
								int e1X = e1.getX();
								int e1Y = e1.getY();
								int e1W = e1.getWidth();
								int e1H = e1.getHeigth();
								
								if (e1Y <= yM + hM && e1Y + e1H >= yM) {//遮挡物至少有出现在我方坦克和敌人的中间
									if (e1X <= xM) {//左边的遮挡物
										if (e1X + e1W >= xM + wM) {//一个遮挡物即可
											//判断到在目标和我方之间有元素可能完全遮挡住视线
											direc = null;
											break;//虽然目标的一部分或全部出现在正下方的某个局域,但是有遮挡物完全遮挡住视线,还是发现不了,所以跳出寻找遮挡物的循环,继续执行寻找目标的循环
										} else if (e1X + e1W > xM && e1X + e1W < xM + wM) {//一个(左边)遮挡物只能遮挡一部分,继续寻找(右边)遮挡物来遮挡
											for (Element e2 : list) {
												if (e2 == this || e2 == e || 
													e2 instanceof Water || e2 instanceof Blast) {
													if (e2 == list.get(list.size()-1)) {
														break;
													}
													continue;
												}
												
												int e2X = e2.getX();
												int e2Y = e2.getY();
												int e2W = e2.getWidth();
												int e2H = e2.getHeigth(); 
												
												if (e2Y <= yM + hM && e2Y + e2H >= yM && //右边遮挡物满足的条件
													e2X <= e1X && e2X + e2W >= xM + wM) {
													direc = null;
													break;
												}
											}
											
											if (direc == null) {
												break;
											}
										}
									} else if (e1X > xM && e1X < xM + wM) {//右边的遮挡物
										for (Element e3 : list) {//寻找左边的遮挡物
											if (e3 == this || e3 == e || 
												e3 instanceof Water || e3 instanceof Blast) {
												if (e3 == list.get(list.size()-1)) {
													break;
												}
												continue;
											}
											
											int e3X = e3.getX();
											int e3Y = e3.getY();
											int e3W = e3.getWidth();
											int e3H = e3.getHeigth(); 
											
											if (e3Y <= yM + hM && e3Y + e3H >= yM &&
												e3X + e3W >= e1X && e3X <= xM) {
												direc = null;
												break;
											}
										}
										
										if (direc == null) {
											break;
										}
									}
									
								} 
								
								if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
									((Target)e).setTargetDirec(direc);
									return (Target)e;
								}
							}
						} else {
							direc = Direction.RIGHT;
						}
						break;
						
					case RIGHT:
						//拿到长矩形的大小和宽度
						int xR = x + w;
						int yR = y;
						int wR = Constant.GAME_WIDTH - x - w;
						int hR = h;
						isCollied = CollisionUtils.isCollsionWithRect(xR, yR, wR, hR,
	                                                                  eX, eY, eW, eH);
						if (isCollied) {//判断到是在右方
							//目标和矩形碰撞并且在矩形内的部分
							int eIX = 0;
							int eIY = 0;
							int eIW = 0;
							int eIH = 0;
							
							if (eY == y) {//目标在正右方
								//拿到目标和矩形碰撞并且在矩形内的部分
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = eH;
							} else if (eY < y) {//目标在右上方
								eIX = eX;
								eIY = y;
								eIW = eW;
								eIH = eY + eH - y;
							} else {//目标在右下方
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = y + h - eY;
							}
							
							//拿到在矩形内的部分的图形和己方之间的处于中间位置的小矩形
							int xM = x + w;
							int yM = eIY;
							int wM = eIX - xM;
							int hM = eIH;
							
							//判断自己方和目标之间是否有阻挡物完全阻挡住视线,注意是完全遮挡住视线!!!
							for (Element e1 : list) {
								if (e1 == this || e1 == e || 
									e1 instanceof Water || e1 instanceof Blast) {//如果遍历到的遮挡物是自己或者是目标或者是水或者是爆炸物,这里东西列为不能遮挡视线的东西
									if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
										((Target)e).setTargetDirec(direc);
										return (Target)e;
									}
									continue;
								}
								
								//拿到遮挡物的坐标和大小
								int e1X = e1.getX();
								int e1Y = e1.getY();
								int e1W = e1.getWidth();
								int e1H = e1.getHeigth();
								
								if (e1X + e1W >= xM && e1X <= xM + wM) {//遮挡物至少有出现在我方坦克和敌人的中间
									if (e1Y <= yM) {//上边的遮挡物
										if (e1Y + e1H >= yM + hM) {//一个遮挡物即可
											//判断到在目标和我方之间有元素可能完全遮挡住视线
											direc = null;
											break;//虽然目标的一部分或全部出现在正右方的某个局域,但是有遮挡物完全遮挡住视线,还是发现不了,所以跳出寻找遮挡物的循环,继续执行寻找目标的循环
										} else if (e1Y + e1H > yM && e1Y + e1H < yM + hM) {//一个(上边)遮挡物只能遮挡一部分,继续寻找(下边)遮挡物来遮挡
											for (Element e2 : list) {
												if (e2 == this || e2 == e || 
													e2 instanceof Water || e2 instanceof Blast) {
													if (e2 == list.get(list.size()-1)) {
														break;
													}
													continue;
												}
												
												int e2X = e2.getX();
												int e2Y = e2.getY();
												int e2W = e2.getWidth();
												int e2H = e2.getHeigth(); 
												
												if (e2X + e2W >= xM && e2X <= xM + wM && //下边遮挡物满足的条件
													e2Y <= e1Y + e1H && e2Y + e2H >= yM + hM) {
													direc = null;
													break;
												}
											}
											
											if (direc == null) {
												break;
											}
										}
									} else if (e1Y > yM && e1Y < yM + hM) {//下边的遮挡物
										for (Element e3 : list) {//上边的遮挡物
											if (e3 == this || e3 == e || 
												e3 instanceof Water || e3 instanceof Blast) {
												if (e3 == list.get(list.size()-1)) {
													break;
												}
												continue;
											}
											
											int e3X = e3.getX();
											int e3Y = e3.getY();
											int e3W = e3.getWidth();
											int e3H = e3.getHeigth(); 
											
											if (e3X + e3W >= xM && e3X <= xM + wM &&
												e3Y + e3H >= e1Y && e3Y <= yM) {
												direc = null;
												break;
											}
										}
										
										if (direc == null) {
											break;
										}
									}
									
								} 
								
								if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
									((Target)e).setTargetDirec(direc);
									return (Target)e;
								}
							}
						} else {
							direc = Direction.LEFT;
						}
						break;
						
					case LEFT:
						//拿到长矩形的大小和宽度
						int xL = 0;
						int yL = y;
						int wL = x;
						int hL = h;
						isCollied = CollisionUtils.isCollsionWithRect(xL, yL, wL, hL,
	                                                                  eX, eY, eW, eH);
						if (isCollied) {//判断到是在左方
							//目标和矩形碰撞并且在矩形内的部分
							int eIX = 0;
							int eIY = 0;
							int eIW = 0;
							int eIH = 0;
							
							if (eY == y) {//目标在正左方
								//拿到目标和矩形碰撞并且在矩形内的部分
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = eH;
							} else if (eY < y) {//目标在左上方
								eIX = eX;
								eIY = y;
								eIW = eW;
								eIH = eY + eH - y;
							} else {//目标在左下方
								eIX = eX;
								eIY = eY;
								eIW = eW;
								eIH = y + h - eY;
							}
							
							//拿到在矩形内的部分的图形和己方之间的处于中间位置的小矩形
							int xM = eIX + eIW;
							int yM = eIY;
							int wM = x - xM;
							int hM = eIH;
							
							//判断自己方和目标之间是否有阻挡物完全阻挡住视线,注意是完全遮挡住视线!!!
							for (Element e1 : list) {
								if (e1 == this || e1 == e || 
									e1 instanceof Water || e1 instanceof Blast) {//如果遍历到的遮挡物是自己或者是目标或者是水或者是爆炸物,这里东西列为不能遮挡视线的东西
									if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
										((Target)e).setTargetDirec(direc);
										return (Target)e;
									}
									continue;
								}
								
								//拿到遮挡物的坐标和大小
								int e1X = e1.getX();
								int e1Y = e1.getY();
								int e1W = e1.getWidth();
								int e1H = e1.getHeigth();
								
								if (e1X + e1W >= xM && e1X <= xM + wM) {//遮挡物至少有出现在我方坦克和敌人的中间
									if (e1Y <= yM) {//上边的遮挡物
										if (e1Y + e1H >= yM + hM) {//一个遮挡物即可
											//判断到在目标和我方之间有元素可能完全遮挡住视线
											direc = null;
											break;//虽然目标的一部分或全部出现在正右方的某个局域,但是有遮挡物完全遮挡住视线,还是发现不了,所以跳出寻找遮挡物的循环,继续执行寻找目标的循环
										} else if (e1Y + e1H > yM && e1Y + e1H < yM + hM) {//一个(上边)遮挡物只能遮挡一部分,继续寻找(下边)遮挡物来遮挡
											for (Element e2 : list) {
												if (e2 == this || e2 == e || 
													e2 instanceof Water || e2 instanceof Blast) {
													if (e2 == list.get(list.size()-1)) {
														break;
													}
													continue;
												}
												
												int e2X = e2.getX();
												int e2Y = e2.getY();
												int e2W = e2.getWidth();
												int e2H = e2.getHeigth(); 
												
												if (e2X + e2W >= xM && e2X <= xM + wM && //下边遮挡物满足的条件
													e2Y <= e1Y + e1H && e2Y + e2H >= yM + hM) {
													direc = null;
													break;
												}
											}
											
											if (direc == null) {
												break;
											}
										}
									} else if (e1Y > yM && e1Y < yM + hM) {//下边的遮挡物
										for (Element e3 : list) {//上边的遮挡物
											if (e3 == this || e3 == e || 
												e3 instanceof Water || e3 instanceof Blast) {
												if (e3 == list.get(list.size()-1)) {
													break;
												}
												continue;
											}
											
											int e3X = e3.getX();
											int e3Y = e3.getY();
											int e3W = e3.getWidth();
											int e3H = e3.getHeigth(); 
											
											if (e3X + e3W >= xM && e3X <= xM + wM &&
												e3Y + e3H >= e1Y && e3Y <= yM) { //上边的遮挡物
												direc = null;
												break;
											}
										}
										
										if (direc == null) {
											break;
										}
									}
									
								} 
								
								if (e1 == list.get(list.size()-1)) {//判断到最后一个元素都没有可以遮挡的物体,即可以发现目标.发现一个目标就好了,不用再去发现另外的目标
									((Target)e).setTargetDirec(direc);
									return (Target)e;
								}
							}
						} else {
							direc = null;
						}
						break;
						
					default:
						break;
					}
				}
			
			}
		}
		return null;//遍历完元素,都没有发现目标在哪里
	}


/*	
	 * 随机移动,会自动避免走下一步碰到子弹的情况
	 
	public void move(ArrayList<Element> list) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMoveTime >= 500) {
			while (true) {//这里用while的目的是为了时间间隔到了后一定要走一步!!!
				int seed = new Random().nextInt(4) + 1; //随机生成1-4之间的数字
				//Direction direc = Direction.UP;
				Direction direc = null;
				switch (seed % 4) {
				case 0:
					direc = Direction.UP;
					break;
				case 1:
					direc = Direction.DOWN;
					break;
				case 2:
					direc = Direction.RIGHT;
					break;
				case 3:
					direc = Direction.LEFT;
					break;
				default:
					break;
				}
				
				if (this.canMove(list, direc)) {
					super.move(direc);
					lastMoveTime = currentTime;
					return;
				}
				
			}
		}
	}*/
	
	/*
	 * 如果没发现目标才随机移动,如果发现了目标并且自己的血量大于1就会向目标方向前进,
	 * 也会自动避免走下一步碰到子弹的情况
	 * @author xiangweizhang
	 * @date 2017.5.14
	 */
	public void autoMove(ArrayList<Element> list) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMoveTime >= 500) {
			//while(true) {
				Direction direc = null;
				
				Target target = this.FindTarget(list);
				if (target != null) {
					if (target instanceof Treasure) {
						Treasure trea = (Treasure)target;
						boolean canEat = this.canEatTeasure(trea, list);
						//System.out.println(canEat);
						if (canEat) {
							direc = target.getTargetDirec();
						} else {
							direc = null;
						}
					} else {
						direc = target.getTargetDirec();
					}	
					//direc = target.getTargetDirec();
				}
				
				//System.out.println(direc);
				
				if (direc == null) {
					int seed = new Random().nextInt(4) + 1; //随机生成1-4之间的数字
					switch (seed % 4) {
					case 0:
						direc = Direction.UP;
						break;
					case 1:
						direc = Direction.DOWN;
						break;
					case 2:
						direc = Direction.RIGHT;
						break;
					case 3:
						direc = Direction.LEFT;
						break;
					default:
						break;
					}
				}
				
				if (this.canMove(list, direc)) {
					super.move(direc);
					lastMoveTime = currentTime;
				}
			//}
		}
	}
	
	/*
	 * 随机移动,不会自动避免走下一步碰到子弹的情况
	 */
	public void autoMove() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMoveTime >= 500) {
				int seed = new Random().nextInt(4) + 1; //随机生成1-4之间的数字
				Direction direc = Direction.UP;
				switch (seed % 4) {
				case 0:
					direc = Direction.UP;
					break;
				case 1:
					direc = Direction.DOWN;
					break;
				case 2:
					direc = Direction.RIGHT;
					break;
				case 3:
					direc = Direction.LEFT;
					break;
				default:
					break;
				}
				
				super.move(direc);
				
				lastMoveTime = currentTime;
		 }
	}
	
	/*
	 * 己方坦克避免射击到自己人
	 * 首先拿到坦克的坐标和大小以及方向,进而得到子弹刚开始的坐标大小方向.
	 * 接着判断子弹所在的区域有没有和其他物体(不能是自己方坦克,如果是,则终止判断,不能射击)碰撞的可能,
	 * 如果有,则可以射击,如果没有,接着变化子弹的坐标,再次判断子弹所在的局域有没有和其他物体碰撞的可能,
	 * 如果有,则可以射击.....最后判断完子弹经过的局域都没有和子弹碰撞的物体,也可以射击
	 * @author xiangweizhang
	 */
	
	private boolean canShot(ArrayList<Element> list) {
		//获得坦克的坐标和大小以及方向
		int tankE_X = this.x;
		int tankE_Y = this.y;
		int tankE_W = this.width;
		int tankE_H = this.height;
		Direction tankE_Direc = this.getDirection();
		
		//获得子弹的坐标和大小以及方向
		int bulletX = 0;
		int bulletY = 0;
		int bulletW = 0;
		int bulletH = 0;
		Direction bullet_Direc = tankE_Direc;
		
		switch (bullet_Direc) {
		case UP:
			try {
				bulletW = DrawUtils.getSize(Constant.IMAGE_BULLET_U_PATH)[0];
				bulletH = DrawUtils.getSize(Constant.IMAGE_BULLET_U_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			bulletX = tankE_X + tankE_W/2 - bulletW/2;
			bulletY = tankE_Y - bulletH;
			break;
		case DOWN:
			try {
				bulletW = DrawUtils.getSize(Constant.IMAGE_BULLET_D_PATH)[0];
				bulletH = DrawUtils.getSize(Constant.IMAGE_BULLET_D_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			bulletX = tankE_X + tankE_W/2 - bulletW/2;
			bulletY = tankE_Y + tankE_H;
			break;
		case RIGHT:
			try {
				bulletW = DrawUtils.getSize(Constant.IMAGE_BULLET_R_PATH)[0];
				bulletH = DrawUtils.getSize(Constant.IMAGE_BULLET_R_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			bulletX = tankE_X + tankE_W;
			bulletY = tankE_Y + tankE_H/2 - bulletH/2;
			break;
		case LEFT:
			try {
				bulletW = DrawUtils.getSize(Constant.IMAGE_BULLET_L_PATH)[0];
				bulletH = DrawUtils.getSize(Constant.IMAGE_BULLET_L_PATH)[1];
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			bulletX = tankE_X - bulletW;
			bulletY = tankE_Y + tankE_H/2 - bulletH/2;
			break;
		default:
			break;
		}
		
		//子弹的坐标变化以及是否碰上可撞物
		switch (bullet_Direc) {
		case UP:
			while(bulletY >= -bulletH) {
				for (Element e : list) {
					if (!(e instanceof Acrossable)) {
						int eX = e.getX();
						int eY = e.getY();
						int eW = e.getWidth();
						int eH = e.getHeigth();
						boolean isCollied = 
								CollisionUtils.isCollsionWithRect(bulletX, bulletY, bulletW, 
										                          bulletH, eX, eY, eW, eH);
					    if (isCollied) {
					    	if (e instanceof TankEnemy) {
					    		return false;
					    	}
					    	
					    	return true;
					    }
					}
				}
				bulletY -= bulletH;
				//System.out.println("up----");
			}
			break;
			
		case DOWN:
			while(bulletY <= Constant.GAME_HEIGHT) {
				for (Element e : list) {
					if (!(e instanceof Acrossable)) {
						int eX = e.getX();
						int eY = e.getY();
						int eW = e.getWidth();
						int eH = e.getHeigth();
						boolean isCollied = 
								CollisionUtils.isCollsionWithRect(bulletX, bulletY, bulletW, 
										                          bulletH, eX, eY, eW, eH);
					    if (isCollied) {
					    	if (e instanceof TankEnemy) {
					    		return false;
					    	}
					    	
					    	return true;
					    }
					}
				}
				bulletY += bulletH;
				//System.out.println("down----");
			}
			break;
			
		case RIGHT:
			while(bulletX <= Constant.GAME_WIDTH) {
				for (Element e : list) {
					if (!(e instanceof Acrossable)) {
						int eX = e.getX();
						int eY = e.getY();
						int eW = e.getWidth();
						int eH = e.getHeigth();
						boolean isCollied = 
								CollisionUtils.isCollsionWithRect(bulletX, bulletY, bulletW, 
										                          bulletH, eX, eY, eW, eH);
					    if (isCollied) {
					    	if (e instanceof TankEnemy) {
					    		return false;
					    	}
					    	
					    	return true;
					    }
					}
				}
				bulletX += bulletW;
				//System.out.println("right----");
			}
			break;	
			
		case LEFT:
			while(bulletX >= -bulletW) {
				for (Element e : list) {
					if (!(e instanceof Acrossable)) {
						int eX = e.getX();
						int eY = e.getY();
						int eW = e.getWidth();
						int eH = e.getHeigth();
						boolean isCollied = 
								CollisionUtils.isCollsionWithRect(bulletX, bulletY, bulletW, 
										                          bulletH, eX, eY, eW, eH);
					    if (isCollied) {
					    	if (e instanceof TankEnemy) {
					    		return false;
					    	}
					    	
					    	return true;
					    }
					}
				}
				bulletX -= bulletW;
				//System.out.println("left----");
			}
			break;
		default:
			break;
		}
	
		return true;
		
	}
	
	/*
	 * 随机射击,不会避免射击到自己人的情况
	 */
	public Bullet autoShot() {
		int randomTimeInterval = new Random().nextInt(2)*5000 + 5000; 
		long currentTime = System.currentTimeMillis();
		Bullet bullet;
		if (currentTime - lastShotTime >= randomTimeInterval) {
			bullet = new Bullet(this);
			try {
				SoundUtils.play(Constant.FIRE_MUSIC);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			lastShotTime = currentTime;
		} else {
			bullet = null;
		}
		
		return bullet;
	} 
	
	/*
	 * 避免射击到自己人
	 * @author xianweizhang
	 * @date 2017.5.14 add:如果发现敌人,射击时间间隔就变短为800ms
	 */
	public Bullet autoShot(ArrayList<Element> list) {
		int randomTimeInterval = 0;
		Target target = this.FindTarget(list);
		if (target != null) {
			if (target instanceof Treasure) {
				return null;
			}
			randomTimeInterval = 800;
		} else {
			randomTimeInterval = new Random().nextInt(2)*5000 + 5000; 
		}
		
		long currentTime = System.currentTimeMillis();
		Bullet bullet = null;
		if (currentTime - lastShotTime >= randomTimeInterval) {
			if (this.canShot(list)) {
				bullet = new Bullet(this);
				try {
					SoundUtils.play(Constant.FIRE_MUSIC);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				lastShotTime = currentTime;
			}
		} else {
			bullet = null;
		}
		
		return bullet;
	} 
	
	@Override
	public void draw() {
		String image = null;
		switch (direction) {
		case UP:
			image = "res/img/enemy_1_u.gif";
			break;
		case DOWN:
			image = "res/img/enemy_1_d.gif";
			break;
		case RIGHT:
			image = "res/img/enemy_1_r.gif";
			break;
		case LEFT:
			image = "res/img/enemy_1_l.gif";
			break;
		default:
			break;
		}
		
		try {
			DrawUtils.draw(image, x, y);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
