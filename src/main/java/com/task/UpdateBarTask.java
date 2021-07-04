package com.task;

import com.engine.DataEngine;
import com.entity.Bar;
import com.engine.DataCache;
import com.utils.MongoDBUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateBarTask implements Runnable
{
	public Logger log = LoggerFactory.getLogger(UpdateBarTask.class);
	@Autowired
	private DataEngine dataEngine ;

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		while(true) {
			try {
				log.info("Bar--Update待处理行情数量:"+DataCache.UPDATEQUEUE.size());
				Bar bar  = DataCache.UPDATEQUEUE.take();
				Document document = MongoDBUtil.beanToDocument(bar);
				Document filter = new Document(); 
				filter.append("id", bar.getId());
				filter.append("symbol", bar.getSymbol());
				filter.append("isClose", 0);
				switch (bar.getType())
				{
					case Bar.TYPE_MIN:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_min_realtime", filter, document);
						break;
					case Bar.TYPE_5MIN:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_min5_realtime", filter, document);
						break;
					case Bar.TYPE_15MIN:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_min15_realtime", filter, document);
						break;
					case Bar.TYPE_30MIN:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_min30_realtime", filter, document);
						break;
					case Bar.TYPE_HOUR:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_min60_realtime", filter, document);
						break;
					case Bar.TYPE_DAY:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_day_realtime", filter, document);
						break;
					case Bar.TYPE_WEEK:
						dataEngine.getMdDBClient().updateOne("quantaxis", "stock_week_realtime", filter, document);
						break;
					default:
						break;
				}
				
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
}
