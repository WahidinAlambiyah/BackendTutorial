import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ServiceService from './service.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IService } from '@/shared/model/service.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ServiceDetails',
  setup() {
    const serviceService = inject('serviceService', () => new ServiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const service: Ref<IService> = ref({});

    const retrieveService = async serviceId => {
      try {
        const res = await serviceService().find(serviceId);
        service.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.serviceId) {
      retrieveService(route.params.serviceId);
    }

    return {
      alertService,
      service,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
