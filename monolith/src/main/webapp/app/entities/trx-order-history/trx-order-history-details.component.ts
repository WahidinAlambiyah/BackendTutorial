import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxOrderHistoryService from './trx-order-history.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxOrderHistory } from '@/shared/model/trx-order-history.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderHistoryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxOrderHistoryService = inject('trxOrderHistoryService', () => new TrxOrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxOrderHistory: Ref<ITrxOrderHistory> = ref({});

    const retrieveTrxOrderHistory = async trxOrderHistoryId => {
      try {
        const res = await trxOrderHistoryService().find(trxOrderHistoryId);
        trxOrderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderHistoryId) {
      retrieveTrxOrderHistory(route.params.trxOrderHistoryId);
    }

    return {
      ...dateFormat,
      alertService,
      trxOrderHistory,

      previousState,
      t$: useI18n().t,
    };
  },
});
