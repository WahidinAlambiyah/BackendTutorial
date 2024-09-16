import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TestimonialService from './testimonial.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITestimonial, Testimonial } from '@/shared/model/testimonial.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestimonialUpdate',
  setup() {
    const testimonialService = inject('testimonialService', () => new TestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const testimonial: Ref<ITestimonial> = ref(new Testimonial());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTestimonial = async testimonialId => {
      try {
        const res = await testimonialService().find(testimonialId);
        res.date = new Date(res.date);
        testimonial.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testimonialId) {
      retrieveTestimonial(route.params.testimonialId);
    }

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      feedback: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      rating: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
        max: validations.maxValue(t$('entity.validation.max', { max: 5 }).toString(), 5),
      },
      date: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, testimonial as any);
    v$.value.$validate();

    return {
      testimonialService,
      alertService,
      testimonial,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: testimonial }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.testimonial.id) {
        this.testimonialService()
          .update(this.testimonial)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.testimonial.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.testimonialService()
          .create(this.testimonial)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.testimonial.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
