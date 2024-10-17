import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstEmployeeService from './mst-employee.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstDepartmentService from '@/entities/mst-department/mst-department.service';
import { type IMstDepartment } from '@/shared/model/mst-department.model';
import { type IMstEmployee, MstEmployee } from '@/shared/model/mst-employee.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstEmployeeUpdate',
  setup() {
    const mstEmployeeService = inject('mstEmployeeService', () => new MstEmployeeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstEmployee: Ref<IMstEmployee> = ref(new MstEmployee());

    const mstEmployees: Ref<IMstEmployee[]> = ref([]);

    const mstDepartmentService = inject('mstDepartmentService', () => new MstDepartmentService());

    const mstDepartments: Ref<IMstDepartment[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstEmployee = async mstEmployeeId => {
      try {
        const res = await mstEmployeeService().find(mstEmployeeId);
        res.hireDate = new Date(res.hireDate);
        mstEmployee.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstEmployeeId) {
      retrieveMstEmployee(route.params.mstEmployeeId);
    }

    const initRelationships = () => {
      mstEmployeeService()
        .retrieve()
        .then(res => {
          mstEmployees.value = res.data;
        });
      mstDepartmentService()
        .retrieve()
        .then(res => {
          mstDepartments.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      firstName: {},
      lastName: {},
      email: {},
      phoneNumber: {},
      hireDate: {},
      salary: {},
      commissionPct: {},
      mstJobs: {},
      manager: {},
      department: {},
      mstDepartment: {},
      jobHistory: {},
    };
    const v$ = useVuelidate(validationRules, mstEmployee as any);
    v$.value.$validate();

    return {
      mstEmployeeService,
      alertService,
      mstEmployee,
      previousState,
      isSaving,
      currentLanguage,
      mstEmployees,
      mstDepartments,
      v$,
      ...useDateFormat({ entityRef: mstEmployee }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstEmployee.id) {
        this.mstEmployeeService()
          .update(this.mstEmployee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstEmployee.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstEmployeeService()
          .create(this.mstEmployee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstEmployee.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
