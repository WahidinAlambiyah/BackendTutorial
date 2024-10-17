import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstDriverService from './mst-driver.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstDriver, MstDriver } from '@/shared/model/mst-driver.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDriverUpdate',
  setup() {
    const mstDriverService = inject('mstDriverService', () => new MstDriverService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstDriver: Ref<IMstDriver> = ref(new MstDriver());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstDriver = async mstDriverId => {
      try {
        const res = await mstDriverService().find(mstDriverId);
        mstDriver.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDriverId) {
      retrieveMstDriver(route.params.mstDriverId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      contactNumber: {},
      vehicleDetails: {},
    };
    const v$ = useVuelidate(validationRules, mstDriver as any);
    v$.value.$validate();

    return {
      mstDriverService,
      alertService,
      mstDriver,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstDriver.id) {
        this.mstDriverService()
          .update(this.mstDriver)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstDriver.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstDriverService()
          .create(this.mstDriver)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstDriver.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
