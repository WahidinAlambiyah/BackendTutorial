import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import SubDistrictService from './sub-district.service';
import { type ISubDistrict } from '@/shared/model/sub-district.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubDistrictDetails',
  setup() {
    const subDistrictService = inject('subDistrictService', () => new SubDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const subDistrict: Ref<ISubDistrict> = ref({});

    const retrieveSubDistrict = async subDistrictId => {
      try {
        const res = await subDistrictService().find(subDistrictId);
        subDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.subDistrictId) {
      retrieveSubDistrict(route.params.subDistrictId);
    }

    return {
      alertService,
      subDistrict,

      previousState,
      t$: useI18n().t,
    };
  },
});
