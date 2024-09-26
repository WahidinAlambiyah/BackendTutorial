import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MstTaskService from './mst-task.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import MstJobService from '@/entities/mst-job/mst-job.service';
import { type IMstJob } from '@/shared/model/mst-job.model';
import { type IMstTask, MstTask } from '@/shared/model/mst-task.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MstTaskUpdate',
  setup() {
    const mstTaskService = inject('mstTaskService', () => new MstTaskService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const mstTask: Ref<IMstTask> = ref(new MstTask());

    const mstJobService = inject('mstJobService', () => new MstJobService());

    const mstJobs: Ref<IMstJob[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMstTask = async mstTaskId => {
      try {
        const res = await mstTaskService().find(mstTaskId);
        mstTask.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.mstTaskId) {
      retrieveMstTask(route.params.mstTaskId);
    }

    const initRelationships = () => {
      mstJobService()
        .retrieve()
        .then(res => {
          mstJobs.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {},
      description: {},
      jobs: {},
    };
    const v$ = useVuelidate(validationRules, mstTask as any);
    v$.value.$validate();

    return {
      mstTaskService,
      alertService,
      mstTask,
      previousState,
      isSaving,
      currentLanguage,
      mstJobs,
      v$,
      t$,
    };
  },
  created(): void {
    this.mstTask.jobs = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.mstTask.id) {
        this.mstTaskService()
          .update(this.mstTask)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('monolithApp.mstTask.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.mstTaskService()
          .create(this.mstTask)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('monolithApp.mstTask.created', { param: param.id }).toString());
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
