import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstRegionService from './mst-region.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstRegion, MstRegion } from '@/shared/model/mst-region.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstRegionUpdate',
  setup() {
    const mstRegionService = inject('mstRegionService', () => new MstRegionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstRegion: Ref<IMstRegion> = ref(new MstRegion());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      unm49Code: {},
      isoAlpha2Code: {},
      countries: {},
    };
    const v$ = useVuelidate(validationRules, mstRegion as any);
    v$.value.$validate();

    return {
      mstRegionService,
      alertService,
      mstRegion,
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
      if (this.mstRegion.id) {
        this.mstRegionService()
          .update(this.mstRegion)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstRegion.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstRegionService()
          .create(this.mstRegion)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstRegion.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
