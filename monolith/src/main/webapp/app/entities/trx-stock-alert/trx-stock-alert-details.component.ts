import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxStockAlertService from './trx-stock-alert.service';
import { type ITrxStockAlert } from '@/shared/model/trx-stock-alert.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxStockAlertDetails',
  setup() {
    const trxStockAlertService = inject('trxStockAlertService', () => new TrxStockAlertService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxStockAlert: Ref<ITrxStockAlert> = ref({});

    const retrieveTrxStockAlert = async trxStockAlertId => {
      try {
        const res = await trxStockAlertService().find(trxStockAlertId);
        trxStockAlert.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxStockAlertId) {
      retrieveTrxStockAlert(route.params.trxStockAlertId);
    }

    return {
      alertService,
      trxStockAlert,

      previousState,
      t$: useI18n().t,
    };
  },
});
