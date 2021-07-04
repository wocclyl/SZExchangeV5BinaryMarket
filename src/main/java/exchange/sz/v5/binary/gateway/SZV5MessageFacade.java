package exchange.sz.v5.binary.gateway;

import com.engine.DataCache;
import com.entity.Tick;
import exchange.sz.v5.binary.annotation.SZV5Message;
import exchange.sz.v5.binary.enums.SZV5MsgType;
import exchange.sz.v5.binary.model.message.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息入口类
 *
 * @author xuejian.sun
 * @date 2019/12/11 14:17
 */
@Slf4j
public class SZV5MessageFacade implements SZV5Gateway {

    private Map<SZV5MsgType, Invoker> invokerMap;

    public SZV5MessageFacade() {
        this.invokerMap = new HashMap<>(16);
        initialize(this);
    }

    public void onMessage(Message message) {
        SZV5MsgType szv5MsgType = message.getMsgType().getEnumValue();
        Invoker invoker = invokerMap.get(szv5MsgType);
        if(invoker != null) {
            invoker.invoker(message);
        }
    }

    @SZV5Message(msgType = SZV5MsgType.CHANNEL_HEARTBEAT)
    @Override
    public void onChannelHeartbeat(SZV5MarketChannelHeartbeat channelHeartbeat) {
        log.info("channel heartbeat -> {}", channelHeartbeat);
    }

    @SZV5Message(msgType = SZV5MsgType.HEARTBEAT)
    @Override
    public void onHeartbeat(SZV5Heartbeat heartbeat) {
        log.debug("receive heartbeat ... {}", heartbeat);
    }

    @Override
    public void connected(String message) {
        log.info("连接成功 -> {}", message);
    }

    @Override
    public void disconnected(String message) {
        log.info("连接断开 -> {}", message);
    }

    @SZV5Message(msgType = SZV5MsgType.LOGON)
    @Override
    public void onLogon(SZV5Logon logon) {
        log.info("登陆成功 -> {}", logon);
    }

    @SZV5Message(msgType = SZV5MsgType.LOGOUT)
    @Override
    public void onLogout(SZV5Logout logout) {
        log.info("登出成功 -> {}", logout);
    }

    @SZV5Message(msgType = SZV5MsgType.LEVEL1)
    @Override
    public void onLevel1Market(SZV5Level1MarketSnapshot level1MarketSnapshot) {
        String securityId = level1MarketSnapshot.getSecurityId().getValue();
        if(securityId.startsWith("000") || securityId.startsWith("300")){
            try {
                // 代码相关
                String symbol = securityId; // 代码
                String exchange ="SZ"; // 交易所代码
                String rtSymbol = symbol+"."+exchange; // 系统中的唯一代码,通常是 合约代码.交易所代码
                Long tradeTime = level1MarketSnapshot.getTradeTime();
                String tradingDay="";       // 交易日
                String actionDay="";        // 业务发生日
                String actionTime = "";       // 时间(HHMMSSmmm)
                DateTime dateTime = DateTime.now();

                Integer status=1; // 状态

                // 成交数据
                Double lastPrice = level1MarketSnapshot.getLastPx(); // 最新成交价
                Integer lastVolume = 0; // 最新成交量
                Integer volume = 0; // 今天总成交量
                Double openInterest = 0d; // 持仓量

                Long preOpenInterest = 0L;// 昨持仓
                Double preClosePrice = 0d; // 前收盘价
                Double preSettlePrice = 0d; // 昨结算

                // 常规行情
                Double openPrice = level1MarketSnapshot.getOpenPx(); // 今日开盘价
                Double highPrice = level1MarketSnapshot.getHighPx(); // 今日最高价
                Double lowPrice = level1MarketSnapshot.getLowPx(); // 今日最低价

                Double upperLimit = 0d; // 涨停价
                Double lowerLimit = 0d; // 跌停价
                Tick tick = new Tick();
                tick.setTickValue("SZ", symbol, exchange, rtSymbol, tradingDay, actionDay, tradeTime, dateTime, 1, lastPrice, lastVolume, volume, preClosePrice, openPrice, highPrice, lowPrice);
                //, bidPrice1, bidPrice2, bidPrice3, bidPrice4, bidPrice5, askPrice1, askPrice2, askPrice3, askPrice4, askPrice5, bidVolume1, bidVolume2, bidVolume3, bidVolume4, bidVolume5, askVolume1, askVolume2, askVolume3, askVolume4, askVolume5

                //DataCache.TICKQUEUE.put(tick);
                DataCache.BARQUEUE.put(tick);

                log.info("level1 -> {}", level1MarketSnapshot);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        if(securityId.equals("000001") || securityId.equals("100504")){
//            log.info("level1 -> {}", level1MarketSnapshot);
//        }
    }

    @Slf4j
    private static class Invoker {

        private final Method method;

        private final Object target;

        private final Class<?> parameterType;

        private Invoker(Method method, Object target) {
            this.method = method;
            this.parameterType = method.getParameterTypes()[0];
            this.target = target;
        }

        private void invoker(Object message) {
            if(!message.getClass().isAssignableFrom(parameterType)) {
                log.warn("当前参数[{}] 与 {}.{}({})中的参数不一致,必须为自身类或子类。currentMessage -> {}"
                        , message.getClass().getName(), target.getClass().getName(),
                        method.getName(), parameterType.getName(), message);
                return;
            }
            try {
                method.setAccessible(true);
                method.invoke(target, message);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("", e);
            }
        }
    }

    private void initialize(SZV5MessageFacade szv5MessageFacade) {
        Class<? extends SZV5MessageFacade> handlerClass = szv5MessageFacade.getClass();
        Method[] methods = handlerClass.getMethods();
        this.invokerMap = Arrays.stream(methods)
                .filter(method -> !Modifier.isPrivate(method.getModifiers())
                        && (method.isAnnotationPresent(SZV5Message.class))
                        && method.getParameters().length == 1)
                .collect(Collectors.toMap(
                        method -> method.getAnnotation(SZV5Message.class).msgType(),
                        method -> new Invoker(method, szv5MessageFacade)));
    }
}
