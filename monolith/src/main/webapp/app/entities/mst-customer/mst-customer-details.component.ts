import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstCustomerService from './mst-customer.service';
import { type IMstCustomer } from '@/shared/model/mst-customer.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCustomerDetails',
  setup() {
    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstCustomer: Ref<IMstCustomer> = ref({});

    const retrieveMstCustomer = async mstCustomerId => {
      try {
        const res = await mstCustomerService().find(mstCustomerId);
        mstCustomer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCustomerId) {
      retrieveMstCustomer(route.params.mstCustomerId);
    }

    return {
      alertService,
      mstCustomer,

      previousState,
      t$: useI18n().t,
    };
  },
});
