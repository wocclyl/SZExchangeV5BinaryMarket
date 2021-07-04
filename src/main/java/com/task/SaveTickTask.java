package com.task;

import com.engine.DataEngine;
import com.entity.Tick;
import com.engine.DataCache;
import org.springframework.stereotype.Service;
import com.utils.MongoDBUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class SaveTickTask implements Runnable{

	public Logger log = LoggerFactory.getLogger(SaveTickTask.class);
	
	@Autowired
	private DataEngine dataEngine;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				log.info("Tick——Save待处理行情数量:"+DataCache.TICKQUEUE.size());
				Tick tick  = DataCache.TICKQUEUE.take();
				DataCache.LASTTICK.put(tick.getSymbol(), tick);
//				Document document = MongoDBUtil.beanToDocument(tick);
//				dataEngine.getMdDBClient().insert("quantaxis", "TICK", document);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
