import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstBrandService from './mst-brand.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstBrand, MstBrand } from '@/shared/model/mst-brand.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstBrandUpdate',
  setup() {
    const mstBrandService = inject('mstBrandService', () => new MstBrandService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstBrand: Ref<IMstBrand> = ref(new MstBrand());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstBrand = async mstBrandId => {
      try {
        const res = await mstBrandService().find(mstBrandId);
        mstBrand.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstBrandId) {
      retrieveMstBrand(route.params.mstBrandId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      logo: {},
      description: {},
    };
    const v$ = useVuelidate(validationRules, mstBrand as any);
    v$.value.$validate();

    return {
      mstBrandService,
      alertService,
      mstBrand,
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
      if (this.mstBrand.id) {
        this.mstBrandService()
          .update(this.mstBrand)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstBrand.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstBrandService()
          .create(this.mstBrand)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstBrand.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
