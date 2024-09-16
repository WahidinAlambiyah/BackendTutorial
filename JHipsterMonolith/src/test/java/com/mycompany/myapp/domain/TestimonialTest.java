package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EventTestSamples.*;
import static com.mycompany.myapp.domain.ServiceTestSamples.*;
import static com.mycompany.myapp.domain.TestimonialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TestimonialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Testimonial.class);
        Testimonial testimonial1 = getTestimonialSample1();
        Testimonial testimonial2 = new Testimonial();
        assertThat(testimonial1).isNotEqualTo(testimonial2);

        testimonial2.setId(testimonial1.getId());
        assertThat(testimonial1).isEqualTo(testimonial2);

        testimonial2 = getTestimonialSample2();
        assertThat(testimonial1).isNotEqualTo(testimonial2);
    }

    @Test
    void eventTest() {
        Testimonial testimonial = getTestimonialRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        testimonial.addEvent(eventBack);
        assertThat(testimonial.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getTestimonial()).isEqualTo(testimonial);

        testimonial.removeEvent(eventBack);
        assertThat(testimonial.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getTestimonial()).isNull();

        testimonial.events(new HashSet<>(Set.of(eventBack)));
        assertThat(testimonial.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getTestimonial()).isEqualTo(testimonial);

        testimonial.setEvents(new HashSet<>());
        assertThat(testimonial.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getTestimonial()).isNull();
    }

    @Test
    void serviceTest() {
        Testimonial testimonial = getTestimonialRandomSampleGenerator();
        Services serviceBack = getServiceRandomSampleGenerator();

        testimonial.addService(serviceBack);
        assertThat(testimonial.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getTestimonial()).isEqualTo(testimonial);

        testimonial.removeService(serviceBack);
        assertThat(testimonial.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getTestimonial()).isNull();

        testimonial.services(new HashSet<>(Set.of(serviceBack)));
        assertThat(testimonial.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getTestimonial()).isEqualTo(testimonial);

        testimonial.setServices(new HashSet<>());
        assertThat(testimonial.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getTestimonial()).isNull();
    }
}
