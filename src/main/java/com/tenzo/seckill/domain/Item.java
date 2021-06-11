package com.tenzo.seckill.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;

public class Item {

    @Id
    Integer id;

    @Version
    Integer version;
    String name;
    String description;
    BigDecimal originalPrice;
    BigDecimal promotePrice;
    Integer stock;
    Integer rate;
    Integer isDelete;

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(BigDecimal promotePrice) {
        this.promotePrice = promotePrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", originalPrice=" + originalPrice +
                ", promotePrice=" + promotePrice +
                ", stock=" + stock +
                ", rate=" + rate +
                ", isDelete=" + isDelete +
                '}';
    }
}
