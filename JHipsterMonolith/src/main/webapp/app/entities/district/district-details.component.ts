import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import DistrictService from './district.service';
import { type IDistrict } from '@/shared/model/district.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DistrictDetails',
  setup() {
    const districtService = inject('districtService', () => new DistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const district: Ref<IDistrict> = ref({});

    const retrieveDistrict = async districtId => {
      try {
        const res = await districtService().find(districtId);
        district.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.districtId) {
      retrieveDistrict(route.params.districtId);
    }

    return {
      alertService,
      district,

      previousState,
      t$: useI18n().t,
    };
  },
});
