package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstProductTestSamples.*;
import static com.mycompany.myapp.domain.MstSupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstSupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstSupplier.class);
        MstSupplier mstSupplier1 = getMstSupplierSample1();
        MstSupplier mstSupplier2 = new MstSupplier();
        assertThat(mstSupplier1).isNotEqualTo(mstSupplier2);

        mstSupplier2.setId(mstSupplier1.getId());
        assertThat(mstSupplier1).isEqualTo(mstSupplier2);

        mstSupplier2 = getMstSupplierSample2();
        assertThat(mstSupplier1).isNotEqualTo(mstSupplier2);
    }

    @Test
    void productTest() {
        MstSupplier mstSupplier = getMstSupplierRandomSampleGenerator();
        MstProduct mstProductBack = getMstProductRandomSampleGenerator();

        mstSupplier.addProduct(mstProductBack);
        assertThat(mstSupplier.getProducts()).containsOnly(mstProductBack);
        assertThat(mstProductBack.getMstSupplier()).isEqualTo(mstSupplier);

        mstSupplier.removeProduct(mstProductBack);
        assertThat(mstSupplier.getProducts()).doesNotContain(mstProductBack);
        assertThat(mstProductBack.getMstSupplier()).isNull();

        mstSupplier.products(new HashSet<>(Set.of(mstProductBack)));
        assertThat(mstSupplier.getProducts()).containsOnly(mstProductBack);
        assertThat(mstProductBack.getMstSupplier()).isEqualTo(mstSupplier);

        mstSupplier.setProducts(new HashSet<>());
        assertThat(mstSupplier.getProducts()).doesNotContain(mstProductBack);
        assertThat(mstProductBack.getMstSupplier()).isNull();
    }
}
