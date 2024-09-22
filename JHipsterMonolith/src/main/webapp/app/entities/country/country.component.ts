import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import CountryService from './country.service';
import { type ICountry } from '@/shared/model/country.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Country',
  setup() {
    const { t: t$ } = useI18n();
    const countryService = inject('countryService', () => new CountryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');
    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const countries: Ref<ICountry[]> = ref([]);

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

    const retrieveCountrys = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = currentSearch.value
          ? await countryService().search(currentSearch.value, paginationQuery)
          : await countryService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        countries.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCountrys();
    };

    onMounted(async () => {
      await retrieveCountrys();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveCountrys();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICountry) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCountry = async () => {
      try {
        await countryService().delete(removeId.value);
        const message = t$('jHipsterMonolithApp.country.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCountrys();
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
        await retrieveCountrys();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveCountrys();
    });

    return {
      countries,
      handleSyncList,
      isFetching,
      retrieveCountrys,
      clear,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCountry,
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
