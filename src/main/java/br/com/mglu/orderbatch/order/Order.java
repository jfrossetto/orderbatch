package br.com.mglu.orderbatch.order;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Builder
@Getter
@Table("orders")
public class Order {

    @Id
    @Column("order_id")
    private Integer orderId;

    @Column("user_id")
    private Integer userId;

    @Column("order_date")
    private LocalDate orderDate;

}
