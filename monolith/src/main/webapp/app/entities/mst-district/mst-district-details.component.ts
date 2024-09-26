import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstDistrictService from './mst-district.service';
import { type IMstDistrict } from '@/shared/model/mst-district.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDistrictDetails',
  setup() {
    const mstDistrictService = inject('mstDistrictService', () => new MstDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstDistrict: Ref<IMstDistrict> = ref({});

    const retrieveMstDistrict = async mstDistrictId => {
      try {
        const res = await mstDistrictService().find(mstDistrictId);
        mstDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDistrictId) {
      retrieveMstDistrict(route.params.mstDistrictId);
    }

    return {
      alertService,
      mstDistrict,

      previousState,
      t$: useI18n().t,
    };
  },
});
