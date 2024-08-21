import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ProvinceService from './province.service';
import { type IProvince } from '@/shared/model/province.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProvinceDetails',
  setup() {
    const provinceService = inject('provinceService', () => new ProvinceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const province: Ref<IProvince> = ref({});

    const retrieveProvince = async provinceId => {
      try {
        const res = await provinceService().find(provinceId);
        province.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.provinceId) {
      retrieveProvince(route.params.provinceId);
    }

    return {
      alertService,
      province,

      previousState,
      t$: useI18n().t,
    };
  },
});
