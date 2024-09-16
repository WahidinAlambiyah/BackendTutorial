import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import EventService from './event.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ServiceService from '@/entities/service/service.service';
import { type IService } from '@/shared/model/service.model';
import TestimonialService from '@/entities/testimonial/testimonial.service';
import { type ITestimonial } from '@/shared/model/testimonial.model';
import { type IEvent, Event } from '@/shared/model/event.model';
import { EventStatus } from '@/shared/model/enumerations/event-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EventUpdate',
  setup() {
    const eventService = inject('eventService', () => new EventService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const event: Ref<IEvent> = ref(new Event());

    const serviceService = inject('serviceService', () => new ServiceService());

    const services: Ref<IService[]> = ref([]);

    const testimonialService = inject('testimonialService', () => new TestimonialService());

    const testimonials: Ref<ITestimonial[]> = ref([]);
    const eventStatusValues: Ref<string[]> = ref(Object.keys(EventStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveEvent = async eventId => {
      try {
        const res = await eventService().find(eventId);
        res.date = new Date(res.date);
        event.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.eventId) {
      retrieveEvent(route.params.eventId);
    }

    const initRelationships = () => {
      serviceService()
        .retrieve()
        .then(res => {
          services.value = res.data;
        });
      testimonialService()
        .retrieve()
        .then(res => {
          testimonials.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      date: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      location: {},
      capacity: {},
      price: {},
      status: {},
      service: {},
      testimonial: {},
    };
    const v$ = useVuelidate(validationRules, event as any);
    v$.value.$validate();

    return {
      eventService,
      alertService,
      event,
      previousState,
      eventStatusValues,
      isSaving,
      currentLanguage,
      services,
      testimonials,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: event }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.event.id) {
        this.eventService()
          .update(this.event)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.event.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.eventService()
          .create(this.event)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.event.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
