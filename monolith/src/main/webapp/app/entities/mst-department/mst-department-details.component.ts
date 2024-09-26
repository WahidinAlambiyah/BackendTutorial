import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstDepartmentService from './mst-department.service';
import { type IMstDepartment } from '@/shared/model/mst-department.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDepartmentDetails',
  setup() {
    const mstDepartmentService = inject('mstDepartmentService', () => new MstDepartmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstDepartment: Ref<IMstDepartment> = ref({});

    const retrieveMstDepartment = async mstDepartmentId => {
      try {
        const res = await mstDepartmentService().find(mstDepartmentId);
        mstDepartment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDepartmentId) {
      retrieveMstDepartment(route.params.mstDepartmentId);
    }

    return {
      alertService,
      mstDepartment,

      previousState,
      t$: useI18n().t,
    };
  },
});
