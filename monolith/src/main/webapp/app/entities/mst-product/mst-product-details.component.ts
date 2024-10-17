import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstProductService from './mst-product.service';
import { type IMstProduct } from '@/shared/model/mst-product.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstProductDetails',
  setup() {
    const mstProductService = inject('mstProductService', () => new MstProductService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstProduct: Ref<IMstProduct> = ref({});

    const retrieveMstProduct = async mstProductId => {
      try {
        const res = await mstProductService().find(mstProductId);
        mstProduct.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstProductId) {
      retrieveMstProduct(route.params.mstProductId);
    }

    return {
      alertService,
      mstProduct,

      previousState,
      t$: useI18n().t,
    };
  },
});
