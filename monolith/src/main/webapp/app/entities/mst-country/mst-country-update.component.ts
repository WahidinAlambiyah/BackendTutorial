import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstCountryService from './mst-country.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstRegionService from '@/entities/mst-region/mst-region.service';
import { type IMstRegion } from '@/shared/model/mst-region.model';
import { type IMstCountry, MstCountry } from '@/shared/model/mst-country.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCountryUpdate',
  setup() {
    const mstCountryService = inject('mstCountryService', () => new MstCountryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstCountry: Ref<IMstCountry> = ref(new MstCountry());

    const mstRegionService = inject('mstRegionService', () => new MstRegionService());

    const mstRegions: Ref<IMstRegion[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstCountry = async mstCountryId => {
      try {
        const res = await mstCountryService().find(mstCountryId);
        mstCountry.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCountryId) {
      retrieveMstCountry(route.params.mstCountryId);
    }

    const initRelationships = () => {
      mstRegionService()
        .retrieve()
        .then(res => {
          mstRegions.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      unm49Code: {},
      isoAlpha2Code: {},
      provinces: {},
      region: {},
    };
    const v$ = useVuelidate(validationRules, mstCountry as any);
    v$.value.$validate();

    return {
      mstCountryService,
      alertService,
      mstCountry,
      previousState,
      isSaving,
      currentLanguage,
      mstRegions,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstCountry.id) {
        this.mstCountryService()
          .update(this.mstCountry)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstCountry.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstCountryService()
          .create(this.mstCountry)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstCountry.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
