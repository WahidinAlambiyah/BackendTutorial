import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstDistrictService from './mst-district.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCityService from '@/entities/mst-city/mst-city.service';
import { type IMstCity } from '@/shared/model/mst-city.model';
import { type IMstDistrict, MstDistrict } from '@/shared/model/mst-district.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDistrictUpdate',
  setup() {
    const mstDistrictService = inject('mstDistrictService', () => new MstDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstDistrict: Ref<IMstDistrict> = ref(new MstDistrict());

    const mstCityService = inject('mstCityService', () => new MstCityService());

    const mstCities: Ref<IMstCity[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstDistrict = async mstDistrictId => {
      try {
        const res = await mstDistrictService().find(mstDistrictId);
        mstDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDistrictId) {
      retrieveMstDistrict(route.params.mstDistrictId);
    }

    const initRelationships = () => {
      mstCityService()
        .retrieve()
        .then(res => {
          mstCities.value = res.data;
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
      subDistricts: {},
      city: {},
    };
    const v$ = useVuelidate(validationRules, mstDistrict as any);
    v$.value.$validate();

    return {
      mstDistrictService,
      alertService,
      mstDistrict,
      previousState,
      isSaving,
      currentLanguage,
      mstCities,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstDistrict.id) {
        this.mstDistrictService()
          .update(this.mstDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstDistrict.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstDistrictService()
          .create(this.mstDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstDistrict.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
