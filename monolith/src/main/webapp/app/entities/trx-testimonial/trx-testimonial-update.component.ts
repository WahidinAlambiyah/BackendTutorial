import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxTestimonialService from './trx-testimonial.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxTestimonial, TrxTestimonial } from '@/shared/model/trx-testimonial.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxTestimonialUpdate',
  setup() {
    const trxTestimonialService = inject('trxTestimonialService', () => new TrxTestimonialService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxTestimonial: Ref<ITrxTestimonial> = ref(new TrxTestimonial());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxTestimonial = async trxTestimonialId => {
      try {
        const res = await trxTestimonialService().find(trxTestimonialId);
        res.date = new Date(res.date);
        trxTestimonial.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxTestimonialId) {
      retrieveTrxTestimonial(route.params.trxTestimonialId);
    }

    const initRelationships = () => {};

    initRelationships();

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
      events: {},
      services: {},
    };
    const v$ = useVuelidate(validationRules, trxTestimonial as any);
    v$.value.$validate();

    return {
      trxTestimonialService,
      alertService,
      trxTestimonial,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: trxTestimonial }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxTestimonial.id) {
        this.trxTestimonialService()
          .update(this.trxTestimonial)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxTestimonial.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxTestimonialService()
          .create(this.trxTestimonial)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxTestimonial.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
