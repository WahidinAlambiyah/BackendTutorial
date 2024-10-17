import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxOrderItemService from './trx-order-item.service';
import { type ITrxOrderItem } from '@/shared/model/trx-order-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxOrderItemDetails',
  setup() {
    const trxOrderItemService = inject('trxOrderItemService', () => new TrxOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxOrderItem: Ref<ITrxOrderItem> = ref({});

    const retrieveTrxOrderItem = async trxOrderItemId => {
      try {
        const res = await trxOrderItemService().find(trxOrderItemId);
        trxOrderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxOrderItemId) {
      retrieveTrxOrderItem(route.params.trxOrderItemId);
    }

    return {
      alertService,
      trxOrderItem,

      previousState,
      t$: useI18n().t,
    };
  },
});
