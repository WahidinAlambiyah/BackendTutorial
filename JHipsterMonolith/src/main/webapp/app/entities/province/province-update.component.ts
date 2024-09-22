import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ProvinceService from './province.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CountryService from '@/entities/country/country.service';
import { type ICountry } from '@/shared/model/country.model';
import { type IProvince, Province } from '@/shared/model/province.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProvinceUpdate',
  setup() {
    const provinceService = inject('provinceService', () => new ProvinceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const province: Ref<IProvince> = ref(new Province());

    const countryService = inject('countryService', () => new CountryService());

    const countries: Ref<ICountry[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProvince = async provinceId => {
      try {
        const res = await provinceService().find(provinceId);
        province.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.provinceId) {
      retrieveProvince(route.params.provinceId);
    }

    const initRelationships = () => {
      countryService()
        .retrieve()
        .then(res => {
          countries.value = res.data;
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
    const v$ = useVuelidate(validationRules, province as any);
    v$.value.$validate();

    return {
      provinceService,
      alertService,
      province,
      previousState,
      isSaving,
      currentLanguage,
      countries,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.province.id) {
        this.provinceService()
          .update(this.province)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.province.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.provinceService()
          .create(this.province)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.province.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
