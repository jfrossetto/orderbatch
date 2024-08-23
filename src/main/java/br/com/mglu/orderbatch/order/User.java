package br.com.mglu.orderbatch.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Builder
@Getter
@EqualsAndHashCode
@Table("users")
public class User {

    @Id
    @Column("user_id")
    private Integer userId;

    private String name;
}
