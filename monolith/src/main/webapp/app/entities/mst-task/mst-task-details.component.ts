import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstTaskService from './mst-task.service';
import { type IMstTask } from '@/shared/model/mst-task.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstTaskDetails',
  setup() {
    const mstTaskService = inject('mstTaskService', () => new MstTaskService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstTask: Ref<IMstTask> = ref({});

    const retrieveMstTask = async mstTaskId => {
      try {
        const res = await mstTaskService().find(mstTaskId);
        mstTask.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstTaskId) {
      retrieveMstTask(route.params.mstTaskId);
    }

    return {
      alertService,
      mstTask,

      previousState,
      t$: useI18n().t,
    };
  },
});
