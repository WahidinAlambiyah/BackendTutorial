import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstPostalCodeService from './mst-postal-code.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstSubDistrictService from '@/entities/mst-sub-district/mst-sub-district.service';
import { type IMstSubDistrict } from '@/shared/model/mst-sub-district.model';
import { type IMstPostalCode, MstPostalCode } from '@/shared/model/mst-postal-code.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstPostalCodeUpdate',
  setup() {
    const mstPostalCodeService = inject('mstPostalCodeService', () => new MstPostalCodeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstPostalCode: Ref<IMstPostalCode> = ref(new MstPostalCode());

    const mstSubDistrictService = inject('mstSubDistrictService', () => new MstSubDistrictService());

    const mstSubDistricts: Ref<IMstSubDistrict[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstPostalCode = async mstPostalCodeId => {
      try {
        const res = await mstPostalCodeService().find(mstPostalCodeId);
        mstPostalCode.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstPostalCodeId) {
      retrieveMstPostalCode(route.params.mstPostalCodeId);
    }

    const initRelationships = () => {
      mstSubDistrictService()
        .retrieve()
        .then(res => {
          mstSubDistricts.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      code: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      subDistrict: {},
    };
    const v$ = useVuelidate(validationRules, mstPostalCode as any);
    v$.value.$validate();

    return {
      mstPostalCodeService,
      alertService,
      mstPostalCode,
      previousState,
      isSaving,
      currentLanguage,
      mstSubDistricts,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstPostalCode.id) {
        this.mstPostalCodeService()
          .update(this.mstPostalCode)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstPostalCode.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstPostalCodeService()
          .create(this.mstPostalCode)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstPostalCode.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
