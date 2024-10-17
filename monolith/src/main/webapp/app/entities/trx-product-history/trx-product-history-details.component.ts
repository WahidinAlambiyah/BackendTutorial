import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxProductHistoryService from './trx-product-history.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxProductHistory } from '@/shared/model/trx-product-history.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxProductHistoryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxProductHistoryService = inject('trxProductHistoryService', () => new TrxProductHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxProductHistory: Ref<ITrxProductHistory> = ref({});

    const retrieveTrxProductHistory = async trxProductHistoryId => {
      try {
        const res = await trxProductHistoryService().find(trxProductHistoryId);
        trxProductHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxProductHistoryId) {
      retrieveTrxProductHistory(route.params.trxProductHistoryId);
    }

    return {
      ...dateFormat,
      alertService,
      trxProductHistory,

      previousState,
      t$: useI18n().t,
    };
  },
});
