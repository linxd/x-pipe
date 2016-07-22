package com.ctrip.xpipe.api.foundation;

import com.ctrip.xpipe.utils.ServicesUtil;

/**
 * basic inforamtion, like dc...
 * 
 * @author wenchao.meng
 *
 * Jun 13, 2016
 */
public interface FoundationService {
	
	public static FoundationService DEFAULT = ServicesUtil.getFoundationService();
	
	/**
	 * get current dc
	 * @return
	 */
	String getDataCenter();

}
