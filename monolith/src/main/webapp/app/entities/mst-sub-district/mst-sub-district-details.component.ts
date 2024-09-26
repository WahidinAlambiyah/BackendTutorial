import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstSubDistrictService from './mst-sub-district.service';
import { type IMstSubDistrict } from '@/shared/model/mst-sub-district.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstSubDistrictDetails',
  setup() {
    const mstSubDistrictService = inject('mstSubDistrictService', () => new MstSubDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstSubDistrict: Ref<IMstSubDistrict> = ref({});

    const retrieveMstSubDistrict = async mstSubDistrictId => {
      try {
        const res = await mstSubDistrictService().find(mstSubDistrictId);
        mstSubDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstSubDistrictId) {
      retrieveMstSubDistrict(route.params.mstSubDistrictId);
    }

    return {
      alertService,
      mstSubDistrict,

      previousState,
      t$: useI18n().t,
    };
  },
});
