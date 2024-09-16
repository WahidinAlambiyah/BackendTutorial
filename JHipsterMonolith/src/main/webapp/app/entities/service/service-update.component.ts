import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ServiceService from './service.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TestimonialService from '@/entities/testimonial/testimonial.service';
import { type ITestimonial } from '@/shared/model/testimonial.model';
import { type IService, Service } from '@/shared/model/service.model';
import { ServiceType } from '@/shared/model/enumerations/service-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ServiceUpdate',
  setup() {
    const serviceService = inject('serviceService', () => new ServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const service: Ref<IService> = ref(new Service());

    const testimonialService = inject('testimonialService', () => new TestimonialService());

    const testimonials: Ref<ITestimonial[]> = ref([]);
    const serviceTypeValues: Ref<string[]> = ref(Object.keys(ServiceType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveService = async serviceId => {
      try {
        const res = await serviceService().find(serviceId);
        service.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.serviceId) {
      retrieveService(route.params.serviceId);
    }

    const initRelationships = () => {
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
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      price: {},
      durationInHours: {},
      serviceType: {},
      testimonial: {},
    };
    const v$ = useVuelidate(validationRules, service as any);
    v$.value.$validate();

    return {
      serviceService,
      alertService,
      service,
      previousState,
      serviceTypeValues,
      isSaving,
      currentLanguage,
      testimonials,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.service.id) {
        this.serviceService()
          .update(this.service)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.service.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.serviceService()
          .create(this.service)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.service.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
