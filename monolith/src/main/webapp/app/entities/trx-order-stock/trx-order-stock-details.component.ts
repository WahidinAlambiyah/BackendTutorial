import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxOrderStockService from './trx-order-stock.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxOrderStock } from '@/shared/model/trx-order-stock.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderStockDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxOrderStockService = inject('trxOrderStockService', () => new TrxOrderStockService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxOrderStock: Ref<ITrxOrderStock> = ref({});

    const retrieveTrxOrderStock = async trxOrderStockId => {
      try {
        const res = await trxOrderStockService().find(trxOrderStockId);
        trxOrderStock.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderStockId) {
      retrieveTrxOrderStock(route.params.trxOrderStockId);
    }

    return {
      ...dateFormat,
      alertService,
      trxOrderStock,

      previousState,
      t$: useI18n().t,
    };
  },
});
