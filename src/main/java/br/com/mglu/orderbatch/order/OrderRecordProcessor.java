package br.com.mglu.orderbatch.order;

import br.com.mglu.orderbatch.textfile.ITextProcessor;
import br.com.mglu.orderbatch.textfile.TextRecordBuilder;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRecordProcessor implements ITextProcessor {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100l);

    private final TextRecordBuilder textProcessor;
    private final OrderRepository repository;

    @Override
    @Transactional
    public boolean processFile(S3Object s3Object) {
        try {
            List<OrderRecord> orderRecords = buildOrderRecords(s3Object);
            log.info(" orderRecords size {}", orderRecords.size());
            persistOrderRecord(orderRecords);
            return true;
        } catch (Exception e) {
            log.error(" error processing {} ", s3Object.getKey(), e);
            return false;
        }
    }

    private List<OrderRecord> buildOrderRecords(S3Object s3Object) {
        BufferedReader newBufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        return newBufferedReader.lines()
                .map(record -> textProcessor.buildRecord(record, OrderRecord.class))
                .collect(Collectors.toList());
    }

    private void persistOrderRecord(List<OrderRecord> orderRecords) {
        Map<String, User> users = new HashMap<>();
        Map<String, Order> orders = new HashMap<>();
        Map<String, OrderProduct> orderProducts = new HashMap<>();
        BiFunction<String, String, String> orderProductKey = (orderId, productId) -> String.format("%s-%s", orderId, productId);
        for (OrderRecord orderRecord : orderRecords) {
            users.computeIfAbsent(orderRecord.userId(), k -> buildUser(orderRecord));
            orders.computeIfAbsent(orderRecord.orderId(), k-> buildRecord(orderRecord));
            orderProducts.computeIfAbsent(orderProductKey.apply(orderRecord.orderDate(), orderRecord.productId()),
                    k -> buildOrderProduct(orderRecord));
        }
        repository.insertUsers(users.values());
        repository.insertOrders(orders.values());
        repository.insertOrderProducts(orderProducts.values());
        log.info("persist done");
    }

    private User buildUser(OrderRecord orderRecord) {
        return User.builder()
                .userId(Integer.valueOf(orderRecord.userId()))
                .name(orderRecord.name().trim())
                .build();
    }

    private Order buildRecord(OrderRecord orderRecord) {
        return Order.builder()
                .orderId(Integer.valueOf(orderRecord.orderId()))
                .userId(Integer.valueOf(orderRecord.userId()))
                .orderDate(LocalDate.parse(orderRecord.orderDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build();
    }

    private OrderProduct buildOrderProduct(OrderRecord orderRecord) {
        return OrderProduct.builder()
                .orderId(Integer.valueOf(orderRecord.orderId()))
                .productId(Integer.valueOf(orderRecord.productId()))
                .productValue(BigDecimal.valueOf(Double.valueOf(orderRecord.orderValue()))
                        .setScale(2, BigDecimal.ROUND_HALF_UP))
                .build();
    }

}
