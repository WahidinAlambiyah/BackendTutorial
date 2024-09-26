import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstCityService from './mst-city.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstProvinceService from '@/entities/mst-province/mst-province.service';
import { type IMstProvince } from '@/shared/model/mst-province.model';
import { type IMstCity, MstCity } from '@/shared/model/mst-city.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstCityUpdate',
  setup() {
    const mstCityService = inject('mstCityService', () => new MstCityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstCity: Ref<IMstCity> = ref(new MstCity());

    const mstProvinceService = inject('mstProvinceService', () => new MstProvinceService());

    const mstProvinces: Ref<IMstProvince[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstCity = async mstCityId => {
      try {
        const res = await mstCityService().find(mstCityId);
        mstCity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstCityId) {
      retrieveMstCity(route.params.mstCityId);
    }

    const initRelationships = () => {
      mstProvinceService()
        .retrieve()
        .then(res => {
          mstProvinces.value = res.data;
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
      districts: {},
      province: {},
    };
    const v$ = useVuelidate(validationRules, mstCity as any);
    v$.value.$validate();

    return {
      mstCityService,
      alertService,
      mstCity,
      previousState,
      isSaving,
      currentLanguage,
      mstProvinces,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstCity.id) {
        this.mstCityService()
          .update(this.mstCity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstCity.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstCityService()
          .create(this.mstCity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstCity.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
