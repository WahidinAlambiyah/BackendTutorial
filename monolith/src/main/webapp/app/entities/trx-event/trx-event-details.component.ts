import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TrxEventService from './trx-event.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type ITrxEvent } from '@/shared/model/trx-event.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxEventDetails',
  setup() {
    const dateFormat = useDateFormat();
    const trxEventService = inject('trxEventService', () => new TrxEventService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const trxEvent: Ref<ITrxEvent> = ref({});

    const retrieveTrxEvent = async trxEventId => {
      try {
        const res = await trxEventService().find(trxEventId);
        trxEvent.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.trxEventId) {
      retrieveTrxEvent(route.params.trxEventId);
    }

    return {
      ...dateFormat,
      alertService,
      trxEvent,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
