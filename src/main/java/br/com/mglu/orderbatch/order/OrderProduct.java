package br.com.mglu.orderbatch.order;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Builder
@Getter
@Table("order_products")
public class OrderProduct {

    @Column("order_id")
    private Integer orderId;

    @Column("product_id")
    private Integer productId;

    @Column("product_value")
    private BigDecimal productValue;

}
