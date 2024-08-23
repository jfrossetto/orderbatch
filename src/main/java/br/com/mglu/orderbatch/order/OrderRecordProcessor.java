package br.com.mglu.orderbatch.order;

import br.com.mglu.orderbatch.textfile.ITextProcessor;
import br.com.mglu.orderbatch.textfile.TextRecordBuilder;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRecordProcessor implements ITextProcessor {

    private final TextRecordBuilder textProcessor;
    private final OrderRepository repository;

    @Override
    @Transactional
    public List<String> processFile(S3Object s3Object) {
        Pair<List<String>, List<OrderRecord>> records = buildOrderRecords(s3Object);
        List<OrderRecord> orderRecords = records.getSecond();
        log.info(" orderRecords size {}", orderRecords.size());
        List<Integer> notInclued = persistOrderRecord(orderRecords);
        return buildLinesNotInclued(records.getFirst(), notInclued);
    }

    private Pair<List<String>, List<OrderRecord>> buildOrderRecords(S3Object s3Object) {
        BufferedReader newBufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        List<String> lines = newBufferedReader.lines().collect(Collectors.toList());
        List<OrderRecord> orderRecords = lines.stream()
                .map(record -> textProcessor.buildRecord(record, OrderRecord.class))
                .collect(Collectors.toList());
        return Pair.of(lines, orderRecords);
    }

    private List<Integer> persistOrderRecord(List<OrderRecord> orderRecords) {
        List<User> users = orderRecords.stream().map(this::buildUser).distinct().toList();
        List<Order> orders = orderRecords.stream().map(this::buildOrder).distinct().toList();
        BiFunction<String, String, String> orderProductKey = (orderId, productId) -> String.format("%s-%s", orderId, productId);
        Map<String, OrderProduct> orderProducts = new HashMap<>();
        List<Integer> notInserted = new ArrayList<>();
        for (int index = 0; index < orderRecords.size(); index++) {
            OrderRecord orderRecord = orderRecords.get(index);
            String orderProductId = orderProductKey.apply(orderRecord.orderId(), orderRecord.productId());
            if(orderProducts.containsKey(orderProductId)) {
                log.warn(" {} {} duplicated ", orderRecord.orderId(), orderRecord.productId());
                notInserted.add(index);
                continue;
            }
            orderProducts.computeIfAbsent(orderProductId, k -> buildOrderProduct(orderRecord));
        }
        repository.insertUsers(users);
        repository.insertOrders(orders);
        repository.insertOrderProducts(orderProducts.values());
        log.info("persist done");
        return notInserted;
    }

    private User buildUser(OrderRecord orderRecord) {
        return User.builder()
                .userId(Integer.valueOf(orderRecord.userId()))
                .name(orderRecord.name().trim())
                .build();
    }

    private Order buildOrder(OrderRecord orderRecord) {
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
                .productValue(BigDecimal.valueOf(Double.parseDouble(orderRecord.orderValue())))
                .build();
    }

    private List<String> buildLinesNotInclued(List<String> lines, List<Integer> notInclued) {
        List<String> linesNotInclued = new ArrayList<>();
        notInclued.forEach(lineNumber -> linesNotInclued.add(lines.get(lineNumber)));
        return linesNotInclued;
    }

}
