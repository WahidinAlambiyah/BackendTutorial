import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstSupplierService from './mst-supplier.service';
import { type IMstSupplier } from '@/shared/model/mst-supplier.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstSupplierDetails',
  setup() {
    const mstSupplierService = inject('mstSupplierService', () => new MstSupplierService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstSupplier: Ref<IMstSupplier> = ref({});

    const retrieveMstSupplier = async mstSupplierId => {
      try {
        const res = await mstSupplierService().find(mstSupplierId);
        mstSupplier.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstSupplierId) {
      retrieveMstSupplier(route.params.mstSupplierId);
    }

    return {
      alertService,
      mstSupplier,

      previousState,
      t$: useI18n().t,
    };
  },
});
