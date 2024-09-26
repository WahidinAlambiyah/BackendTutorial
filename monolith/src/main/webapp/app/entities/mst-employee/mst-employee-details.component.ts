import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstEmployeeService from './mst-employee.service';
import { useDateFormat } from '@/shared/composables';
import { type IMstEmployee } from '@/shared/model/mst-employee.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstEmployeeDetails',
  setup() {
    const dateFormat = useDateFormat();
    const mstEmployeeService = inject('mstEmployeeService', () => new MstEmployeeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstEmployee: Ref<IMstEmployee> = ref({});

    const retrieveMstEmployee = async mstEmployeeId => {
      try {
        const res = await mstEmployeeService().find(mstEmployeeId);
        mstEmployee.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstEmployeeId) {
      retrieveMstEmployee(route.params.mstEmployeeId);
    }

    return {
      ...dateFormat,
      alertService,
      mstEmployee,

      previousState,
      t$: useI18n().t,
    };
  },
});
