import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PostalCodeService from './postal-code.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import SubDistrictService from '@/entities/sub-district/sub-district.service';
import { type ISubDistrict } from '@/shared/model/sub-district.model';
import { type IPostalCode, PostalCode } from '@/shared/model/postal-code.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PostalCodeUpdate',
  setup() {
    const postalCodeService = inject('postalCodeService', () => new PostalCodeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const postalCode: Ref<IPostalCode> = ref(new PostalCode());

    const subDistrictService = inject('subDistrictService', () => new SubDistrictService());

    const subDistricts: Ref<ISubDistrict[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePostalCode = async postalCodeId => {
      try {
        const res = await postalCodeService().find(postalCodeId);
        postalCode.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.postalCodeId) {
      retrievePostalCode(route.params.postalCodeId);
    }

    const initRelationships = () => {
      subDistrictService()
        .retrieve()
        .then(res => {
          subDistricts.value = res.data;
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
    const v$ = useVuelidate(validationRules, postalCode as any);
    v$.value.$validate();

    return {
      postalCodeService,
      alertService,
      postalCode,
      previousState,
      isSaving,
      currentLanguage,
      subDistricts,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.postalCode.id) {
        this.postalCodeService()
          .update(this.postalCode)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jHipsterMonolithApp.postalCode.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.postalCodeService()
          .create(this.postalCode)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jHipsterMonolithApp.postalCode.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
