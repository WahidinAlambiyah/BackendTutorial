package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstBrandTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstBrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstBrand.class);
        MstBrand mstBrand1 = getMstBrandSample1();
        MstBrand mstBrand2 = new MstBrand();
        assertThat(mstBrand1).isNotEqualTo(mstBrand2);

        mstBrand2.setId(mstBrand1.getId());
        assertThat(mstBrand1).isEqualTo(mstBrand2);

        mstBrand2 = getMstBrandSample2();
        assertThat(mstBrand1).isNotEqualTo(mstBrand2);
    }
}
