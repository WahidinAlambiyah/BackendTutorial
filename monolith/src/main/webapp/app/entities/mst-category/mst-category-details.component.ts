import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import MstCategoryService from './mst-category.service';
import { type IMstCategory } from '@/shared/model/mst-category.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCategoryDetails',
  setup() {
    const mstCategoryService = inject('mstCategoryService', () => new MstCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const mstCategory: Ref<IMstCategory> = ref({});

    const retrieveMstCategory = async mstCategoryId => {
      try {
        const res = await mstCategoryService().find(mstCategoryId);
        mstCategory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCategoryId) {
      retrieveMstCategory(route.params.mstCategoryId);
    }

    return {
      alertService,
      mstCategory,

      previousState,
      t$: useI18n().t,
    };
  },
});
