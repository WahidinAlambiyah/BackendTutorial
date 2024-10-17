package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstBrandTestSamples.*;
import static com.mycompany.myapp.domain.MstCategoryTestSamples.*;
import static com.mycompany.myapp.domain.MstProductTestSamples.*;
import static com.mycompany.myapp.domain.MstSupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstProduct.class);
        MstProduct mstProduct1 = getMstProductSample1();
        MstProduct mstProduct2 = new MstProduct();
        assertThat(mstProduct1).isNotEqualTo(mstProduct2);

        mstProduct2.setId(mstProduct1.getId());
        assertThat(mstProduct1).isEqualTo(mstProduct2);

        mstProduct2 = getMstProductSample2();
        assertThat(mstProduct1).isNotEqualTo(mstProduct2);
    }

    @Test
    void categoryTest() {
        MstProduct mstProduct = getMstProductRandomSampleGenerator();
        MstCategory mstCategoryBack = getMstCategoryRandomSampleGenerator();

        mstProduct.setCategory(mstCategoryBack);
        assertThat(mstProduct.getCategory()).isEqualTo(mstCategoryBack);

        mstProduct.category(null);
        assertThat(mstProduct.getCategory()).isNull();
    }

    @Test
    void brandTest() {
        MstProduct mstProduct = getMstProductRandomSampleGenerator();
        MstBrand mstBrandBack = getMstBrandRandomSampleGenerator();

        mstProduct.setBrand(mstBrandBack);
        assertThat(mstProduct.getBrand()).isEqualTo(mstBrandBack);

        mstProduct.brand(null);
        assertThat(mstProduct.getBrand()).isNull();
    }

    @Test
    void mstSupplierTest() {
        MstProduct mstProduct = getMstProductRandomSampleGenerator();
        MstSupplier mstSupplierBack = getMstSupplierRandomSampleGenerator();

        mstProduct.setMstSupplier(mstSupplierBack);
        assertThat(mstProduct.getMstSupplier()).isEqualTo(mstSupplierBack);

        mstProduct.mstSupplier(null);
        assertThat(mstProduct.getMstSupplier()).isNull();
    }
}
