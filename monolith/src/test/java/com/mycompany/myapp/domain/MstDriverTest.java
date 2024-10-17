package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstDriverTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MstDriverTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstDriver.class);
        MstDriver mstDriver1 = getMstDriverSample1();
        MstDriver mstDriver2 = new MstDriver();
        assertThat(mstDriver1).isNotEqualTo(mstDriver2);

        mstDriver2.setId(mstDriver1.getId());
        assertThat(mstDriver1).isEqualTo(mstDriver2);

        mstDriver2 = getMstDriverSample2();
        assertThat(mstDriver1).isNotEqualTo(mstDriver2);
    }
}
