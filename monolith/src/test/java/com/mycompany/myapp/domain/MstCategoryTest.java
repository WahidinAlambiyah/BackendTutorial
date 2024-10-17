package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstCategory.class);
        MstCategory mstCategory1 = getMstCategorySample1();
        MstCategory mstCategory2 = new MstCategory();
        assertThat(mstCategory1).isNotEqualTo(mstCategory2);

        mstCategory2.setId(mstCategory1.getId());
        assertThat(mstCategory1).isEqualTo(mstCategory2);

        mstCategory2 = getMstCategorySample2();
        assertThat(mstCategory1).isNotEqualTo(mstCategory2);
    }
}
