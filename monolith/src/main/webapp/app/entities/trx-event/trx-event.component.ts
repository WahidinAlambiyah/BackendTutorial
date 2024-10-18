import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
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
    const selectedStatus = ref('');  // New status filter
    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const trxEvents: Ref<ITrxEvent[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
      page.value = 1;
      selectedStatus.value = '';  // Reset status filter
    };

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    // Updated retrieve method to include status filter
    const retrieveTrxEvents = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = currentSearch.value
          ? await trxEventService().search(currentSearch.value, paginationQuery)
          : await trxEventService().retrieve(paginationQuery);
        
        // Apply status filter if selectedStatus is set
        console.log('DATA' + selectedStatus.value)
        trxEvents.value = selectedStatus.value
          ? res.data.filter(event => event.status === selectedStatus.value)
          : res.data;

        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    // New method for filtering by status
    const filterByStatus = () => {
      page.value = 1;  // Reset to first page on filter change
      retrieveTrxEvents();
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

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveTrxEvents();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveTrxEvents();
    });

    return {
      trxEvents,
      handleSyncList,
      isFetching,
      retrieveTrxEvents,
      clear,
      filterByStatus,  // Added this for status filter dropdown
      selectedStatus,  // Added this for the status dropdown
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrxEvent,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      ...dataUtils,
    };
  },
});
