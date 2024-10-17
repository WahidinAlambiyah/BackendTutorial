import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxCouponService from './trx-coupon.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxCoupon } from '@/shared/model/trx-coupon.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxCouponDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxCouponService = inject('trxCouponService', () => new TrxCouponService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxCoupon: Ref<ITrxCoupon> = ref({});

    const retrieveTrxCoupon = async trxCouponId => {
      try {
        const res = await trxCouponService().find(trxCouponId);
        trxCoupon.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxCouponId) {
      retrieveTrxCoupon(route.params.trxCouponId);
    }

    return {
      ...dateFormat,
      alertService,
      trxCoupon,

      previousState,
      t$: useI18n().t,
    };
  },
});
