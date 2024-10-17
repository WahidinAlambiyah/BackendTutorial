import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TrxCouponService from './trx-coupon.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ITrxCoupon, TrxCoupon } from '@/shared/model/trx-coupon.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxCouponUpdate',
  setup() {
    const trxCouponService = inject('trxCouponService', () => new TrxCouponService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const trxCoupon: Ref<ITrxCoupon> = ref(new TrxCoupon());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTrxCoupon = async trxCouponId => {
      try {
        const res = await trxCouponService().find(trxCouponId);
        res.validUntil = new Date(res.validUntil);
        trxCoupon.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxCouponId) {
      retrieveTrxCoupon(route.params.trxCouponId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      code: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      discountAmount: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      validUntil: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      minPurchase: {},
    };
    const v$ = useVuelidate(validationRules, trxCoupon as any);
    v$.value.$validate();

    return {
      trxCouponService,
      alertService,
      trxCoupon,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: trxCoupon }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.trxCoupon.id) {
        this.trxCouponService()
          .update(this.trxCoupon)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.trxCoupon.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.trxCouponService()
          .create(this.trxCoupon)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.trxCoupon.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
