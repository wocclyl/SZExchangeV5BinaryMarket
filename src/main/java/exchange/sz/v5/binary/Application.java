package exchange.sz.v5.binary;

import com.config.AppConfig;
import com.engine.InitData;
import com.task.CreateBarTask;
import com.task.SaveBarTask;
import com.task.SaveTickTask;
import com.task.UpdateBarTask;
import exchange.sz.v5.binary.config.SZExchangeConfigure;
import exchange.sz.v5.binary.gateway.SZV5MarketBootstrap;
import exchange.sz.v5.binary.gateway.SZV5MessageFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * @author xuejian.sun
 * @date 2019/12/4 16:11
 */
public class Application {

    public static void main(String[] args) throws Exception {

        SZExchangeConfigure configure = new SZExchangeConfigure();
        configure.setReconnect(5);
        configure.setReconnectInterval(10);
        configure.setServerHost("101.133.170.131");
        configure.setServerPort(8002);
        configure.setHeartbeatInterval(3);
        configure.setPassword("");
        configure.setSenderCompId("test");
        configure.setTargetCompId("test_Send");
        configure.setVersion("1.01");
        SZV5MarketBootstrap bootstrap = SZV5MarketBootstrap.getInstance(configure,new SZV5MessageFacade());
        Runtime.getRuntime().addShutdownHook(new Thread(bootstrap::logout));
        boolean connect = bootstrap.connect();
        if(connect){
            bootstrap.login();
        }
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        //ctx.getBean(InitData.class).initData();
//        SaveTickTask task = ctx.getBean(SaveTickTask.class);
//        Thread thead = new Thread(task);
//        thead.start();

        CreateBarTask task2 = ctx.getBean(CreateBarTask.class);
        Thread thead2 = new Thread(task2);
        thead2.start();


//        UpdateBarTask task3 = ctx.getBean(UpdateBarTask.class);
//        Thread thead3 = new Thread(task3);
//        thead3.start();

        SaveBarTask task4 = ctx.getBean(SaveBarTask.class);
        Thread thead4 = new Thread(task4);
        thead4.start();

        new CountDownLatch(1).await();

    }
}
