package cn.stevekung.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_product")
public class Product implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30)
    private String name;

    @Column
    private Integer firstPrice;

    @Column
    private Date creatTime;

    public Product(String name, Integer firstPrice){
        this.name = name;
        this.firstPrice = firstPrice;
        this.creatTime = new Date();
    }
}
