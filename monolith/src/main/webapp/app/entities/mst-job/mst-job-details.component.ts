import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstJobService from './mst-job.service';
import { type IMstJob } from '@/shared/model/mst-job.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstJobDetails',
  setup() {
    const mstJobService = inject('mstJobService', () => new MstJobService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstJob: Ref<IMstJob> = ref({});

    const retrieveMstJob = async mstJobId => {
      try {
        const res = await mstJobService().find(mstJobId);
        mstJob.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstJobId) {
      retrieveMstJob(route.params.mstJobId);
    }

    return {
      alertService,
      mstJob,

      previousState,
      t$: useI18n().t,
    };
  },
});
