package org.zxw.business;

import org.zxw.bean.Tank;

public interface Treasure extends Target{
	public boolean isHided(Hiddenable hide);
	public boolean isEaten(Tank tank);
}
