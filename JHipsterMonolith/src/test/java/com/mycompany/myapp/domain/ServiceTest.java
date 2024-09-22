package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EventTestSamples.*;
import static com.mycompany.myapp.domain.ServiceTestSamples.*;
import static com.mycompany.myapp.domain.TestimonialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Services.class);
        Services service1 = getServiceSample1();
        Services service2 = new Services();
        assertThat(service1).isNotEqualTo(service2);

        service2.setId(service1.getId());
        assertThat(service1).isEqualTo(service2);

        service2 = getServiceSample2();
        assertThat(service1).isNotEqualTo(service2);
    }

    @Test
    void testimonialTest() {
        Services service = getServiceRandomSampleGenerator();
        Testimonial testimonialBack = getTestimonialRandomSampleGenerator();

        service.setTestimonial(testimonialBack);
        assertThat(service.getTestimonial()).isEqualTo(testimonialBack);

        service.testimonial(null);
        assertThat(service.getTestimonial()).isNull();
    }

    @Test
    void eventTest() {
        Services service = getServiceRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        service.addEvent(eventBack);
        assertThat(service.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getService()).isEqualTo(service);

        service.removeEvent(eventBack);
        assertThat(service.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getService()).isNull();

        service.events(new HashSet<>(Set.of(eventBack)));
        assertThat(service.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getService()).isEqualTo(service);

        service.setEvents(new HashSet<>());
        assertThat(service.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getService()).isNull();
    }
}
