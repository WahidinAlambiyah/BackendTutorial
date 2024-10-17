import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstSupplierService from './mst-supplier.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IMstSupplier, MstSupplier } from '@/shared/model/mst-supplier.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstSupplierUpdate',
  setup() {
    const mstSupplierService = inject('mstSupplierService', () => new MstSupplierService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstSupplier: Ref<IMstSupplier> = ref(new MstSupplier());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstSupplier = async mstSupplierId => {
      try {
        const res = await mstSupplierService().find(mstSupplierId);
        mstSupplier.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstSupplierId) {
      retrieveMstSupplier(route.params.mstSupplierId);
    }

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      contactInfo: {},
      address: {},
      rating: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
        max: validations.maxValue(t$('entity.validation.max', { max: 5 }).toString(), 5),
      },
      products: {},
    };
    const v$ = useVuelidate(validationRules, mstSupplier as any);
    v$.value.$validate();

    return {
      mstSupplierService,
      alertService,
      mstSupplier,
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
      if (this.mstSupplier.id) {
        this.mstSupplierService()
          .update(this.mstSupplier)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstSupplier.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstSupplierService()
          .create(this.mstSupplier)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstSupplier.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
