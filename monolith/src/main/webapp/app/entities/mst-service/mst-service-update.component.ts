import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstServiceService from './mst-service.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TrxTestimonialService from '@/entities/trx-testimonial/trx-testimonial.service';
import { type ITrxTestimonial } from '@/shared/model/trx-testimonial.model';
import { type IMstService, MstService } from '@/shared/model/mst-service.model';
import { ServiceType } from '@/shared/model/enumerations/service-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstServiceUpdate',
  setup() {
    const mstServiceService = inject('mstServiceService', () => new MstServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstService: Ref<IMstService> = ref(new MstService());

    const trxTestimonialService = inject('trxTestimonialService', () => new TrxTestimonialService());

    const trxTestimonials: Ref<ITrxTestimonial[]> = ref([]);
    const serviceTypeValues: Ref<string[]> = ref(Object.keys(ServiceType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstService = async mstServiceId => {
      try {
        const res = await mstServiceService().find(mstServiceId);
        mstService.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstServiceId) {
      retrieveMstService(route.params.mstServiceId);
    }

    const initRelationships = () => {
      trxTestimonialService()
        .retrieve()
        .then(res => {
          trxTestimonials.value = res.data;
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
      events: {},
    };
    const v$ = useVuelidate(validationRules, mstService as any);
    v$.value.$validate();

    return {
      mstServiceService,
      alertService,
      mstService,
      previousState,
      serviceTypeValues,
      isSaving,
      currentLanguage,
      trxTestimonials,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstService.id) {
        this.mstServiceService()
          .update(this.mstService)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstService.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstServiceService()
          .create(this.mstService)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstService.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
