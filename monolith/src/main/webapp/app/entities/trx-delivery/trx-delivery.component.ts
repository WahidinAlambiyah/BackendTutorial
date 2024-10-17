import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import TrxDeliveryService from './trx-delivery.service';
import { type ITrxDelivery } from '@/shared/model/trx-delivery.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TrxDelivery',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const trxDeliveryService = inject('trxDeliveryService', () => new TrxDeliveryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');
    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const trxDeliveries: Ref<ITrxDelivery[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const retrieveTrxDeliverys = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = currentSearch.value
          ? await trxDeliveryService().search(currentSearch.value, paginationQuery)
          : await trxDeliveryService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        trxDeliveries.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTrxDeliverys();
    };

    onMounted(async () => {
      await retrieveTrxDeliverys();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTrxDeliverys();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITrxDelivery) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTrxDelivery = async () => {
      try {
        await trxDeliveryService().delete(removeId.value);
        const message = t$('monolithApp.trxDelivery.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTrxDeliverys();
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
        await retrieveTrxDeliverys();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveTrxDeliverys();
    });

    return {
      trxDeliveries,
      handleSyncList,
      isFetching,
      retrieveTrxDeliverys,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTrxDelivery,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
    };
  },
});
