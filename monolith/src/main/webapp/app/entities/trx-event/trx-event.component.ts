import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TrxEventService from './trx-event.service';
import { type ITrxEvent } from '@/shared/model/trx-event.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxEvent',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const trxEventService = inject('trxEventService', () => new TrxEventService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const trxEvents: Ref<ITrxEvent[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTrxEvents = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await trxEventService().search(currentSearch.value) : await trxEventService().retrieve();
        trxEvents.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTrxEvents();
    };

    onMounted(async () => {
      await retrieveTrxEvents();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTrxEvents();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITrxEvent) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTrxEvent = async () => {
      try {
        await trxEventService().delete(removeId.value);
        const message = t$('monolithApp.trxEvent.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTrxEvents();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      trxEvents,
      handleSyncList,
      isFetching,
      retrieveTrxEvents,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrxEvent,
      t$,
      ...dataUtils,
    };
  },
});
