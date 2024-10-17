import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstLoyaltyProgramService from './mst-loyalty-program.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstCustomerService from '@/entities/mst-customer/mst-customer.service';
import { type IMstCustomer } from '@/shared/model/mst-customer.model';
import { type IMstLoyaltyProgram, MstLoyaltyProgram } from '@/shared/model/mst-loyalty-program.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstLoyaltyProgramUpdate',
  setup() {
    const mstLoyaltyProgramService = inject('mstLoyaltyProgramService', () => new MstLoyaltyProgramService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstLoyaltyProgram: Ref<IMstLoyaltyProgram> = ref(new MstLoyaltyProgram());

    const mstCustomerService = inject('mstCustomerService', () => new MstCustomerService());

    const mstCustomers: Ref<IMstCustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstLoyaltyProgram = async mstLoyaltyProgramId => {
      try {
        const res = await mstLoyaltyProgramService().find(mstLoyaltyProgramId);
        mstLoyaltyProgram.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstLoyaltyProgramId) {
      retrieveMstLoyaltyProgram(route.params.mstLoyaltyProgramId);
    }

    const initRelationships = () => {
      mstCustomerService()
        .retrieve()
        .then(res => {
          mstCustomers.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      pointsEarned: {},
      membershipTier: {},
      customer: {},
    };
    const v$ = useVuelidate(validationRules, mstLoyaltyProgram as any);
    v$.value.$validate();

    return {
      mstLoyaltyProgramService,
      alertService,
      mstLoyaltyProgram,
      previousState,
      isSaving,
      currentLanguage,
      mstCustomers,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstLoyaltyProgram.id) {
        this.mstLoyaltyProgramService()
          .update(this.mstLoyaltyProgram)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstLoyaltyProgram.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstLoyaltyProgramService()
          .create(this.mstLoyaltyProgram)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstLoyaltyProgram.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
