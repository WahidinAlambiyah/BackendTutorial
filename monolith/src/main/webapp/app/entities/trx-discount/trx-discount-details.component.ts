import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxDiscountService from './trx-discount.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxDiscount } from '@/shared/model/trx-discount.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxDiscountDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxDiscountService = inject('trxDiscountService', () => new TrxDiscountService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxDiscount: Ref<ITrxDiscount> = ref({});

    const retrieveTrxDiscount = async trxDiscountId => {
      try {
        const res = await trxDiscountService().find(trxDiscountId);
        trxDiscount.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxDiscountId) {
      retrieveTrxDiscount(route.params.trxDiscountId);
    }

    return {
      ...dateFormat,
      alertService,
      trxDiscount,

      previousState,
      t$: useI18n().t,
    };
  },
});
