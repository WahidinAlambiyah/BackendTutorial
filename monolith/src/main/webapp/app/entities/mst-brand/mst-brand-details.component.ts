import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstBrandService from './mst-brand.service';
import { type IMstBrand } from '@/shared/model/mst-brand.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstBrandDetails',
  setup() {
    const mstBrandService = inject('mstBrandService', () => new MstBrandService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstBrand: Ref<IMstBrand> = ref({});

    const retrieveMstBrand = async mstBrandId => {
      try {
        const res = await mstBrandService().find(mstBrandId);
        mstBrand.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstBrandId) {
      retrieveMstBrand(route.params.mstBrandId);
    }

    return {
      alertService,
      mstBrand,

      previousState,
      t$: useI18n().t,
    };
  },
});
