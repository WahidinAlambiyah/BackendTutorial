import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxDiscountService from './trx-discount.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxDiscount, TrxDiscount } from '@/shared/model/trx-discount.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxDiscountUpdate',
  setup() {
    const trxDiscountService = inject('trxDiscountService', () => new TrxDiscountService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxDiscount: Ref<ITrxDiscount> = ref(new TrxDiscount());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxDiscount = async trxDiscountId => {
      try {
        const res = await trxDiscountService().find(trxDiscountId);
        res.startDate = new Date(res.startDate);
        res.endDate = new Date(res.endDate);
        trxDiscount.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxDiscountId) {
      retrieveTrxDiscount(route.params.trxDiscountId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      discountPercentage: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      startDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      endDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, trxDiscount as any);
    v$.value.$validate();

    return {
      trxDiscountService,
      alertService,
      trxDiscount,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: trxDiscount }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxDiscount.id) {
        this.trxDiscountService()
          .update(this.trxDiscount)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxDiscount.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxDiscountService()
          .create(this.trxDiscount)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxDiscount.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
