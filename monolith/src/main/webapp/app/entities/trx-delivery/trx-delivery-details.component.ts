import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxDeliveryService from './trx-delivery.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxDelivery } from '@/shared/model/trx-delivery.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxDeliveryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxDeliveryService = inject('trxDeliveryService', () => new TrxDeliveryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxDelivery: Ref<ITrxDelivery> = ref({});

    const retrieveTrxDelivery = async trxDeliveryId => {
      try {
        const res = await trxDeliveryService().find(trxDeliveryId);
        trxDelivery.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxDeliveryId) {
      retrieveTrxDelivery(route.params.trxDeliveryId);
    }

    return {
      ...dateFormat,
      alertService,
      trxDelivery,

      previousState,
      t$: useI18n().t,
    };
  },
});
