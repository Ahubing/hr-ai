package com.open.hr.ai.processor;

import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;

import javax.servlet.http.HttpServletResponse;

public interface BossNewMessageProcessor {


    ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req);

}
