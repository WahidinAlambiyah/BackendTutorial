import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstCategoryService from './mst-category.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstCategory, MstCategory } from '@/shared/model/mst-category.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCategoryUpdate',
  setup() {
    const mstCategoryService = inject('mstCategoryService', () => new MstCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstCategory: Ref<IMstCategory> = ref(new MstCategory());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
    };
    const v$ = useVuelidate(validationRules, mstCategory as any);
    v$.value.$validate();

    return {
      mstCategoryService,
      alertService,
      mstCategory,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstCategory.id) {
        this.mstCategoryService()
          .update(this.mstCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstCategory.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstCategoryService()
          .create(this.mstCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstCategory.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
