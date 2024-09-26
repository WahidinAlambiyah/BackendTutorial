import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstDepartmentService from './mst-department.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import LocationService from '@/entities/location/location.service';
import { type ILocation } from '@/shared/model/location.model';
import { type IMstDepartment, MstDepartment } from '@/shared/model/mst-department.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstDepartmentUpdate',
  setup() {
    const mstDepartmentService = inject('mstDepartmentService', () => new MstDepartmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstDepartment: Ref<IMstDepartment> = ref(new MstDepartment());

    const locationService = inject('locationService', () => new LocationService());

    const locations: Ref<ILocation[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstDepartment = async mstDepartmentId => {
      try {
        const res = await mstDepartmentService().find(mstDepartmentId);
        mstDepartment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstDepartmentId) {
      retrieveMstDepartment(route.params.mstDepartmentId);
    }

    const initRelationships = () => {
      locationService()
        .retrieve()
        .then(res => {
          locations.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      departmentName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      location: {},
      mstEmployees: {},
      jobHistory: {},
    };
    const v$ = useVuelidate(validationRules, mstDepartment as any);
    v$.value.$validate();

    return {
      mstDepartmentService,
      alertService,
      mstDepartment,
      previousState,
      isSaving,
      currentLanguage,
      locations,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstDepartment.id) {
        this.mstDepartmentService()
          .update(this.mstDepartment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstDepartment.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstDepartmentService()
          .create(this.mstDepartment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstDepartment.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
