<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstTask.home.createOrEditLabel"
          data-cy="MstTaskCreateUpdateHeading"
          v-text="t$('monolithApp.mstTask.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstTask.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstTask.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstTask.title')" for="mst-task-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="mst-task-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstTask.description')" for="mst-task-description"></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="mst-task-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('monolithApp.mstTask.job')" for="mst-task-job"></label>
            <select
              class="form-control"
              id="mst-task-jobs"
              data-cy="job"
              multiple
              name="job"
              v-if="mstTask.jobs !== undefined"
              v-model="mstTask.jobs"
            >
              <option v-bind:value="getSelected(mstTask.jobs, mstJobOption, 'id')" v-for="mstJobOption in mstJobs" :key="mstJobOption.id">
                {{ mstJobOption.id }}
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
<script lang="ts" src="./mst-task-update.component.ts"></script>
