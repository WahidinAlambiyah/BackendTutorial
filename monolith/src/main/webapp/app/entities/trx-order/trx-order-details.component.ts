import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxOrderService from './trx-order.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxOrder } from '@/shared/model/trx-order.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxOrderService = inject('trxOrderService', () => new TrxOrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxOrder: Ref<ITrxOrder> = ref({});

    const retrieveTrxOrder = async trxOrderId => {
      try {
        const res = await trxOrderService().find(trxOrderId);
        trxOrder.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderId) {
      retrieveTrxOrder(route.params.trxOrderId);
    }

    return {
      ...dateFormat,
      alertService,
      trxOrder,

      previousState,
      t$: useI18n().t,
    };
  },
});
