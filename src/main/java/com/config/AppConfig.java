package com.config;

import com.engine.InitData;
import com.task.CreateBarTask;
import com.task.SaveBarTask;
import com.task.SaveTickTask;
import com.task.UpdateBarTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com")
public class AppConfig {
	
	@Bean
	public SaveTickTask getTickSaveTask() {
		return new SaveTickTask();
	}
	
	@Bean
	public SaveBarTask getBarSaveTask() {
		return new SaveBarTask();
	}
	
	@Bean
	public UpdateBarTask getBarUpdateTask() {
		return new UpdateBarTask();
	}
	
	@Bean
	public CreateBarTask getCreateBarTask() {
		return new CreateBarTask();
	}
	
	@Bean
	public InitData getInitData() {
		return new InitData();
	}
}
