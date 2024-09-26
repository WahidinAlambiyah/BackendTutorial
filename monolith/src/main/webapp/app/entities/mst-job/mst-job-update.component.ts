import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstJobService from './mst-job.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstTaskService from '@/entities/mst-task/mst-task.service';
import { type IMstTask } from '@/shared/model/mst-task.model';
import MstEmployeeService from '@/entities/mst-employee/mst-employee.service';
import { type IMstEmployee } from '@/shared/model/mst-employee.model';
import { type IMstJob, MstJob } from '@/shared/model/mst-job.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstJobUpdate',
  setup() {
    const mstJobService = inject('mstJobService', () => new MstJobService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstJob: Ref<IMstJob> = ref(new MstJob());

    const mstTaskService = inject('mstTaskService', () => new MstTaskService());

    const mstTasks: Ref<IMstTask[]> = ref([]);

    const mstEmployeeService = inject('mstEmployeeService', () => new MstEmployeeService());

    const mstEmployees: Ref<IMstEmployee[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstJob = async mstJobId => {
      try {
        const res = await mstJobService().find(mstJobId);
        mstJob.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstJobId) {
      retrieveMstJob(route.params.mstJobId);
    }

    const initRelationships = () => {
      mstTaskService()
        .retrieve()
        .then(res => {
          mstTasks.value = res.data;
        });
      mstEmployeeService()
        .retrieve()
        .then(res => {
          mstEmployees.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      jobTitle: {},
      minSalary: {},
      maxSalary: {},
      tasks: {},
      employee: {},
      jobHistory: {},
    };
    const v$ = useVuelidate(validationRules, mstJob as any);
    v$.value.$validate();

    return {
      mstJobService,
      alertService,
      mstJob,
      previousState,
      isSaving,
      currentLanguage,
      mstTasks,
      mstEmployees,
      v$,
      t$,
    };
  },
  created(): void {
    this.mstJob.tasks = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstJob.id) {
        this.mstJobService()
          .update(this.mstJob)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstJob.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstJobService()
          .create(this.mstJob)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstJob.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
