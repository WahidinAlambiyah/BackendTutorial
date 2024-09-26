import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstSubDistrictService from './mst-sub-district.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstDistrictService from '@/entities/mst-district/mst-district.service';
import { type IMstDistrict } from '@/shared/model/mst-district.model';
import { type IMstSubDistrict, MstSubDistrict } from '@/shared/model/mst-sub-district.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstSubDistrictUpdate',
  setup() {
    const mstSubDistrictService = inject('mstSubDistrictService', () => new MstSubDistrictService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstSubDistrict: Ref<IMstSubDistrict> = ref(new MstSubDistrict());

    const mstDistrictService = inject('mstDistrictService', () => new MstDistrictService());

    const mstDistricts: Ref<IMstDistrict[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstSubDistrict = async mstSubDistrictId => {
      try {
        const res = await mstSubDistrictService().find(mstSubDistrictId);
        mstSubDistrict.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstSubDistrictId) {
      retrieveMstSubDistrict(route.params.mstSubDistrictId);
    }

    const initRelationships = () => {
      mstDistrictService()
        .retrieve()
        .then(res => {
          mstDistricts.value = res.data;
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
      postalCodes: {},
      district: {},
    };
    const v$ = useVuelidate(validationRules, mstSubDistrict as any);
    v$.value.$validate();

    return {
      mstSubDistrictService,
      alertService,
      mstSubDistrict,
      previousState,
      isSaving,
      currentLanguage,
      mstDistricts,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstSubDistrict.id) {
        this.mstSubDistrictService()
          .update(this.mstSubDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstSubDistrict.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstSubDistrictService()
          .create(this.mstSubDistrict)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstSubDistrict.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
