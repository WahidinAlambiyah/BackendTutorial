<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstJob.home.createOrEditLabel"
          data-cy="MstJobCreateUpdateHeading"
          v-text="t$('monolithApp.mstJob.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstJob.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstJob.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstJob.jobTitle')" for="mst-job-jobTitle"></label>
            <input
              type="text"
              class="form-control"
              name="jobTitle"
              id="mst-job-jobTitle"
              data-cy="jobTitle"
              :class="{ valid: !v$.jobTitle.$invalid, invalid: v$.jobTitle.$invalid }"
              v-model="v$.jobTitle.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstJob.minSalary')" for="mst-job-minSalary"></label>
            <input
              type="number"
              class="form-control"
              name="minSalary"
              id="mst-job-minSalary"
              data-cy="minSalary"
              :class="{ valid: !v$.minSalary.$invalid, invalid: v$.minSalary.$invalid }"
              v-model.number="v$.minSalary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstJob.maxSalary')" for="mst-job-maxSalary"></label>
            <input
              type="number"
              class="form-control"
              name="maxSalary"
              id="mst-job-maxSalary"
              data-cy="maxSalary"
              :class="{ valid: !v$.maxSalary.$invalid, invalid: v$.maxSalary.$invalid }"
              v-model.number="v$.maxSalary.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('monolithApp.mstJob.task')" for="mst-job-task"></label>
            <select
              class="form-control"
              id="mst-job-tasks"
              data-cy="task"
              multiple
              name="task"
              v-if="mstJob.tasks !== undefined"
              v-model="mstJob.tasks"
            >
              <option
                v-bind:value="getSelected(mstJob.tasks, mstTaskOption, 'id')"
                v-for="mstTaskOption in mstTasks"
                :key="mstTaskOption.id"
              >
                {{ mstTaskOption.title }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstJob.employee')" for="mst-job-employee"></label>
            <select class="form-control" id="mst-job-employee" data-cy="employee" name="employee" v-model="mstJob.employee">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="mstJob.employee && mstEmployeeOption.id === mstJob.employee.id ? mstJob.employee : mstEmployeeOption"
                v-for="mstEmployeeOption in mstEmployees"
                :key="mstEmployeeOption.id"
              >
                {{ mstEmployeeOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-job-update.component.ts"></script>
