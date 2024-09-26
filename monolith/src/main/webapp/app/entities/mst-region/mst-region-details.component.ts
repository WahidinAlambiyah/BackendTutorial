import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstRegionService from './mst-region.service';
import { type IMstRegion } from '@/shared/model/mst-region.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstRegionDetails',
  setup() {
    const mstRegionService = inject('mstRegionService', () => new MstRegionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstRegion: Ref<IMstRegion> = ref({});

    const retrieveMstRegion = async mstRegionId => {
      try {
        const res = await mstRegionService().find(mstRegionId);
        mstRegion.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstRegionId) {
      retrieveMstRegion(route.params.mstRegionId);
    }

    return {
      alertService,
      mstRegion,

      previousState,
      t$: useI18n().t,
    };
  },
});
