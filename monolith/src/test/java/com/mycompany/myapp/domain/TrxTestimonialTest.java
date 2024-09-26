package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MstServiceTestSamples.*;
import static com.mycompany.myapp.domain.TrxEventTestSamples.*;
import static com.mycompany.myapp.domain.TrxTestimonialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrxTestimonialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrxTestimonial.class);
        TrxTestimonial trxTestimonial1 = getTrxTestimonialSample1();
        TrxTestimonial trxTestimonial2 = new TrxTestimonial();
        assertThat(trxTestimonial1).isNotEqualTo(trxTestimonial2);

        trxTestimonial2.setId(trxTestimonial1.getId());
        assertThat(trxTestimonial1).isEqualTo(trxTestimonial2);

        trxTestimonial2 = getTrxTestimonialSample2();
        assertThat(trxTestimonial1).isNotEqualTo(trxTestimonial2);
    }

    @Test
    void eventTest() {
        TrxTestimonial trxTestimonial = getTrxTestimonialRandomSampleGenerator();
        TrxEvent trxEventBack = getTrxEventRandomSampleGenerator();

        trxTestimonial.addEvent(trxEventBack);
        assertThat(trxTestimonial.getEvents()).containsOnly(trxEventBack);
        assertThat(trxEventBack.getTestimonial()).isEqualTo(trxTestimonial);

        trxTestimonial.removeEvent(trxEventBack);
        assertThat(trxTestimonial.getEvents()).doesNotContain(trxEventBack);
        assertThat(trxEventBack.getTestimonial()).isNull();

        trxTestimonial.events(new HashSet<>(Set.of(trxEventBack)));
        assertThat(trxTestimonial.getEvents()).containsOnly(trxEventBack);
        assertThat(trxEventBack.getTestimonial()).isEqualTo(trxTestimonial);

        trxTestimonial.setEvents(new HashSet<>());
        assertThat(trxTestimonial.getEvents()).doesNotContain(trxEventBack);
        assertThat(trxEventBack.getTestimonial()).isNull();
    }

    @Test
    void serviceTest() {
        TrxTestimonial trxTestimonial = getTrxTestimonialRandomSampleGenerator();
        MstService mstServiceBack = getMstServiceRandomSampleGenerator();

        trxTestimonial.addService(mstServiceBack);
        assertThat(trxTestimonial.getServices()).containsOnly(mstServiceBack);
        assertThat(mstServiceBack.getTestimonial()).isEqualTo(trxTestimonial);

        trxTestimonial.removeService(mstServiceBack);
        assertThat(trxTestimonial.getServices()).doesNotContain(mstServiceBack);
        assertThat(mstServiceBack.getTestimonial()).isNull();

        trxTestimonial.services(new HashSet<>(Set.of(mstServiceBack)));
        assertThat(trxTestimonial.getServices()).containsOnly(mstServiceBack);
        assertThat(mstServiceBack.getTestimonial()).isEqualTo(trxTestimonial);

        trxTestimonial.setServices(new HashSet<>());
        assertThat(trxTestimonial.getServices()).doesNotContain(mstServiceBack);
        assertThat(mstServiceBack.getTestimonial()).isNull();
    }
}
