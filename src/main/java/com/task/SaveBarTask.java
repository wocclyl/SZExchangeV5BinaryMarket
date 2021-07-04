package com.task;

import com.engine.DataEngine;
import com.entity.Bar;
import com.engine.DataCache;
import com.utils.MongoDBUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveBarTask implements Runnable
{
	public Logger log = LoggerFactory.getLogger(SaveBarTask.class);
	@Autowired
	private DataEngine dataEngine ;

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		while(true) {
			try {
				log.info("Bar——Save待处理行情数量:"+DataCache.SAVEQUEUE.size());
				Bar bar  = DataCache.SAVEQUEUE.take();
				Document document = MongoDBUtil.beanToDocument(bar);
				switch (bar.getType())
				{
					case Bar.TYPE_MIN:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_min_realtime", document);
						break;
					case Bar.TYPE_5MIN:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_min5_realtime", document);
						break;
					case Bar.TYPE_15MIN:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_min15_realtime", document);
						break;
					case Bar.TYPE_30MIN:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_min30_realtime", document);
						break;
					case Bar.TYPE_HOUR:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_min60_realtime", document);
						break;
					case Bar.TYPE_DAY:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_day_realtime", document);
						break;
					case Bar.TYPE_WEEK:
						dataEngine.getMdDBClient().insert("quantaxis", "stock_week_realtime", document);
						break;
					default:
						break;
				}
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
}
