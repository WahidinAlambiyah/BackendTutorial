import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxNotificationService from './trx-notification.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxNotification } from '@/shared/model/trx-notification.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxNotificationDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxNotificationService = inject('trxNotificationService', () => new TrxNotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxNotification: Ref<ITrxNotification> = ref({});

    const retrieveTrxNotification = async trxNotificationId => {
      try {
        const res = await trxNotificationService().find(trxNotificationId);
        trxNotification.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxNotificationId) {
      retrieveTrxNotification(route.params.trxNotificationId);
    }

    return {
      ...dateFormat,
      alertService,
      trxNotification,

      previousState,
      t$: useI18n().t,
    };
  },
});
