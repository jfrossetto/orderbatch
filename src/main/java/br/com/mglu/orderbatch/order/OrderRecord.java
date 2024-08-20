package br.com.mglu.orderbatch.order;

import br.com.mglu.orderbatch.textfile.TextField;
import br.com.mglu.orderbatch.textfile.TextRecord;

@TextRecord(length = 95)
public record OrderRecord(
        @TextField(startAt = 0, endAt = 9) String userId,
        @TextField(startAt = 10, endAt = 54) String name,
        @TextField(startAt = 55, endAt = 64) String orderId,
        @TextField(startAt = 65, endAt = 74) String productId,
        @TextField(startAt = 75, endAt = 86) String orderValue,
        @TextField(startAt = 87, endAt = 94) String orderDate
) {}
