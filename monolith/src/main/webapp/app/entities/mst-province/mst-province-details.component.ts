import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstProvinceService from './mst-province.service';
import { type IMstProvince } from '@/shared/model/mst-province.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstProvinceDetails',
  setup() {
    const mstProvinceService = inject('mstProvinceService', () => new MstProvinceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstProvince: Ref<IMstProvince> = ref({});

    const retrieveMstProvince = async mstProvinceId => {
      try {
        const res = await mstProvinceService().find(mstProvinceId);
        mstProvince.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstProvinceId) {
      retrieveMstProvince(route.params.mstProvinceId);
    }

    return {
      alertService,
      mstProvince,

      previousState,
      t$: useI18n().t,
    };
  },
});
