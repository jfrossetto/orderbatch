package br.com.mglu.orderbatch.order;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public int[][] insertUsers(List<User> users) {
        return jdbcTemplate.batchUpdate(
                "insert into users (user_id, name) values (?, ?)",
                users,
                50,
                (PreparedStatement ps, User user) -> {
                    ps.setInt(1, user.getUserId());
                    ps.setString(2, user.getName());
                });
    }

    public int[][] insertOrders(List<Order> orders) {
        return jdbcTemplate.batchUpdate(
                "insert into orders (order_id, user_id, order_date) values (?, ?, ?)",
                orders,
                50,
                (PreparedStatement ps, Order order) -> {
                    ps.setInt(1, order.getOrderId());
                    ps.setInt(2, order.getUserId());
                    ps.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
                });
    }

    public int[][] insertOrderProducts(Collection<OrderProduct> orderProducts) {
        return jdbcTemplate.batchUpdate(
                "insert into order_products (order_id, product_id, product_value) values (?, ?, ?)",
                orderProducts,
                50,
                (PreparedStatement ps, OrderProduct orderProduct) -> {
                    ps.setInt(1, orderProduct.getOrderId());
                    ps.setInt(2, orderProduct.getProductId());
                    ps.setBigDecimal(3, orderProduct.getProductValue());
                });
    }

}
