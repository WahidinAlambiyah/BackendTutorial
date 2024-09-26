import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstProvinceService from './mst-province.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCountryService from '@/entities/mst-country/mst-country.service';
import { type IMstCountry } from '@/shared/model/mst-country.model';
import { type IMstProvince, MstProvince } from '@/shared/model/mst-province.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstProvinceUpdate',
  setup() {
    const mstProvinceService = inject('mstProvinceService', () => new MstProvinceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstProvince: Ref<IMstProvince> = ref(new MstProvince());

    const mstCountryService = inject('mstCountryService', () => new MstCountryService());

    const mstCountries: Ref<IMstCountry[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstProvince = async mstProvinceId => {
      try {
        const res = await mstProvinceService().find(mstProvinceId);
        mstProvince.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstProvinceId) {
      retrieveMstProvince(route.params.mstProvinceId);
    }

    const initRelationships = () => {
      mstCountryService()
        .retrieve()
        .then(res => {
          mstCountries.value = res.data;
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
      cities: {},
      country: {},
    };
    const v$ = useVuelidate(validationRules, mstProvince as any);
    v$.value.$validate();

    return {
      mstProvinceService,
      alertService,
      mstProvince,
      previousState,
      isSaving,
      currentLanguage,
      mstCountries,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstProvince.id) {
        this.mstProvinceService()
          .update(this.mstProvince)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstProvince.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstProvinceService()
          .create(this.mstProvince)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstProvince.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
