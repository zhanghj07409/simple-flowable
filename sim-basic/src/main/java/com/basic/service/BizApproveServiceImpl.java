/*
 * Copyright (c) 2017 CeYing and/or its affiliates. All rights reserved.
 * CeYing PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.basic.service;


import com.sim.common.service.WorkflowService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Component("BizApproveServiceImpl")
public class BizApproveServiceImpl implements WorkflowService {


	/**
	 * 工作流中的同意的回调函数
	 * @param data
	 */
    @Override
	public Map submitCallBack(Map<String, Object> data) throws Exception {

        String tmpUrid = data.get("urid").toString();
        String userId = data.get("userId").toString();

        return null;
	}


}
