package com.example.tomatomall.po.compositKey;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class SpecificationId implements Serializable {

    private Integer productId;

    private Integer id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecificationId that = (SpecificationId) o;
        return Objects.equals(id, that.id) && Objects.equals(productId, that.productId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, productId);
    }
}

