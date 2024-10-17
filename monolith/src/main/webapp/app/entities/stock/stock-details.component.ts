import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import StockService from './stock.service';
import { useDateFormat } from '@/shared/composables';
import { type IStock } from '@/shared/model/stock.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StockDetails',
  setup() {
    const dateFormat = useDateFormat();
    const stockService = inject('stockService', () => new StockService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const stock: Ref<IStock> = ref({});

    const retrieveStock = async stockId => {
      try {
        const res = await stockService().find(stockId);
        stock.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.stockId) {
      retrieveStock(route.params.stockId);
    }

    return {
      ...dateFormat,
      alertService,
      stock,

      previousState,
      t$: useI18n().t,
    };
  },
});
