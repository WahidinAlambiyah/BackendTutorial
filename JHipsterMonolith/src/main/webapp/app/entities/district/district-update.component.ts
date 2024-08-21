import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import DistrictService from './district.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CityService from '@/entities/city/city.service';
import { type ICity } from '@/shared/model/city.model';
import { type IDistrict, District } from '@/shared/model/district.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DistrictUpdate',
  setup() {
    const districtService = inject('districtService', () => new DistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const district: Ref<IDistrict> = ref(new District());

    const cityService = inject('cityService', () => new CityService());

    const cities: Ref<ICity[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveDistrict = async districtId => {
      try {
        const res = await districtService().find(districtId);
        district.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.districtId) {
      retrieveDistrict(route.params.districtId);
    }

    const initRelationships = () => {
      cityService()
        .retrieve()
        .then(res => {
          cities.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      code: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      city: {},
    };
    const v$ = useVuelidate(validationRules, district as any);
    v$.value.$validate();

    return {
      districtService,
      alertService,
      district,
      previousState,
      isSaving,
      currentLanguage,
      cities,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.district.id) {
        this.districtService()
          .update(this.district)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.district.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.districtService()
          .create(this.district)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.district.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
