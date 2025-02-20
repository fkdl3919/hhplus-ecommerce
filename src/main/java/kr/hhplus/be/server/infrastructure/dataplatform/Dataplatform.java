package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.domain.order.Order;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Dataplatform {

    public static boolean sendData(Long orderKey){
        log.info("Send order data to Dataplatform: " + orderKey);
        return true;
    }

}
