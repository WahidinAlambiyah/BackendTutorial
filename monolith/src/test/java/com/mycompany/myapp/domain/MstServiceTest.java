package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstServiceTestSamples.*;
import static com.mycompany.myapp.domain.TrxEventTestSamples.*;
import static com.mycompany.myapp.domain.TrxTestimonialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MstServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MstService.class);
        MstService mstService1 = getMstServiceSample1();
        MstService mstService2 = new MstService();
        assertThat(mstService1).isNotEqualTo(mstService2);

        mstService2.setId(mstService1.getId());
        assertThat(mstService1).isEqualTo(mstService2);

        mstService2 = getMstServiceSample2();
        assertThat(mstService1).isNotEqualTo(mstService2);
    }

    @Test
    void testimonialTest() {
        MstService mstService = getMstServiceRandomSampleGenerator();
        TrxTestimonial trxTestimonialBack = getTrxTestimonialRandomSampleGenerator();

        mstService.setTestimonial(trxTestimonialBack);
        assertThat(mstService.getTestimonial()).isEqualTo(trxTestimonialBack);

        mstService.testimonial(null);
        assertThat(mstService.getTestimonial()).isNull();
    }

    @Test
    void eventTest() {
        MstService mstService = getMstServiceRandomSampleGenerator();
        TrxEvent trxEventBack = getTrxEventRandomSampleGenerator();

        mstService.addEvent(trxEventBack);
        assertThat(mstService.getEvents()).containsOnly(trxEventBack);
        assertThat(trxEventBack.getService()).isEqualTo(mstService);

        mstService.removeEvent(trxEventBack);
        assertThat(mstService.getEvents()).doesNotContain(trxEventBack);
        assertThat(trxEventBack.getService()).isNull();

        mstService.events(new HashSet<>(Set.of(trxEventBack)));
        assertThat(mstService.getEvents()).containsOnly(trxEventBack);
        assertThat(trxEventBack.getService()).isEqualTo(mstService);

        mstService.setEvents(new HashSet<>());
        assertThat(mstService.getEvents()).doesNotContain(trxEventBack);
        assertThat(trxEventBack.getService()).isNull();
    }
}
