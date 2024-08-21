import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import SubDistrictService from './sub-district.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import DistrictService from '@/entities/district/district.service';
import { type IDistrict } from '@/shared/model/district.model';
import { type ISubDistrict, SubDistrict } from '@/shared/model/sub-district.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SubDistrictUpdate',
  setup() {
    const subDistrictService = inject('subDistrictService', () => new SubDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const subDistrict: Ref<ISubDistrict> = ref(new SubDistrict());

    const districtService = inject('districtService', () => new DistrictService());

    const districts: Ref<IDistrict[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveSubDistrict = async subDistrictId => {
      try {
        const res = await subDistrictService().find(subDistrictId);
        subDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.subDistrictId) {
      retrieveSubDistrict(route.params.subDistrictId);
    }

    const initRelationships = () => {
      districtService()
        .retrieve()
        .then(res => {
          districts.value = res.data;
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
      district: {},
    };
    const v$ = useVuelidate(validationRules, subDistrict as any);
    v$.value.$validate();

    return {
      subDistrictService,
      alertService,
      subDistrict,
      previousState,
      isSaving,
      currentLanguage,
      districts,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.subDistrict.id) {
        this.subDistrictService()
          .update(this.subDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.subDistrict.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.subDistrictService()
          .create(this.subDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.subDistrict.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
